package com.ing.cerebro.workshop.core

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hazelcast.config.Config
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.VertxOptions
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager


object ContentTypes {
    const val json = "application/json"
    const val plainText = "text/plain"
    const val html = "text/html"
    const val form = "application/x-www-form-urlencoded"
}

interface RouterService : Loggable {
    fun finalize(): Router
}

object RetrieverConfig {
    val options = ConfigRetrieverOptions().apply {
        scanPeriod = 5000
        addStore(
            ConfigStoreOptions().apply {
                type = "file"
                format = "properties"
                config = jsonObjectOf("path" to "application.properties")
            })
        addStore(
            ConfigStoreOptions().apply {
                type = "env"
                isOptional = true
            }
        )
        /*addStore(
            ConfigStoreOptions().apply {
                type = "configmap"
                isOptional = true
                val name = System.getenv("configName") ?: "config"
                config = jsonObjectOf("namespace" to "reactive-workshop", "name" to name)
            })*/
    }
}

interface Loggable {
    val logger: Logger  // abstract required field
    fun logger(): Logger {
        return LoggerFactory.getLogger(this::class.java)
    }
}

fun clusterConfig(config: Config): ClusterManager = HazelcastClusterManager(
    config.setProperty("hazelcast.logging.type", "slf4j")
)

fun createClusterManager(options: VertxOptions, mgr: ClusterManager, clusterHost: String = "localhost"): VertxOptions {
    return options.apply {
        clusterManager = mgr
        eventBusOptions.host = clusterHost
        eventBusOptions.port = 5701
        eventBusOptions.clusterPublicHost = clusterHost
        eventBusOptions.clusterPublicPort = 5701
    }
}

val isKubeEnvironment: Boolean by lazy { System.getenv().containsKey("KUBERNETES_SERVICE_HOST") }
val kubeConfig: (String) -> Config = {
    Config().apply {
        networkConfig.join.multicastConfig.isEnabled = false
        networkConfig.join.kubernetesConfig.isEnabled = true
        networkConfig.join.kubernetesConfig.apply {
            setProperty("namespace", "reactive-workshop")
            setProperty("service-name", it)
            setProperty("service-port", 5701.toString())
        }
        networkConfig.port = 5701
    }
}
val localConfig: Config = Config().apply {
    networkConfig.join.multicastConfig.isEnabled = false
    networkConfig.join.tcpIpConfig.isEnabled = true
    networkConfig.join.tcpIpConfig.addMember(System.getenv("HOSTNAME") ?: "localhost")
    networkConfig.join.tcpIpConfig.addMember("localhost")
    networkConfig.join.tcpIpConfig.addMember("localhost:5701")
}

data class Order(val id: String, val type: OrderType, val status: OrderStatus, val customer: String)

enum class OrderStatus { PENDING, PICKED_UP, HOT, COLD }
enum class OrderType { COFFEE, TEA, LATTE, CHOCOLATE_MILK, MILK }

fun prepareJsonMapper() {
    DatabindCodec.mapper().apply {
        registerKotlinModule()
    }
    DatabindCodec.prettyMapper().apply {
        registerKotlinModule()
    }
}