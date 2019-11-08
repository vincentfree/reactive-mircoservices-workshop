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
        eventBusOptions.isClustered = true
        //TESTING resolve of ip
        when(isKubeEnvironment) {
            true -> {
                eventBusOptions.host = System.getenv("HOSTNAME")
                eventBusOptions.clusterPublicHost = System.getenv("HOSTNAME")
//        eventBusOptions.host = InetAddress.getByName(System.getenv("HOSTNAME")).hostAddress //"0.0.0.0" //clusterHost
//        eventBusOptions.port = 18001
//        eventBusOptions.clusterPublicHost = System.getenv("HAZELCAST_SERVICE_NAME").replace("-hazelcast","") //clusterHost
//        eventBusOptions.clusterPublicHost = System.getenv("HAZELCAST_EVENTBUS_SERVICE_HOST") //clusterHost
//        eventBusOptions.clusterPublicPort = 5701
            }
            false -> {
                eventBusOptions.host = clusterHost
//                eventBusOptions.port = 5701
                eventBusOptions.clusterPublicHost = clusterHost
//                eventBusOptions.clusterPublicPort = 5701
            }
        }
    }
}

val isKubeEnvironment: Boolean by lazy { System.getenv().containsKey("KUBERNETES_SERVICE_HOST") }

val kubeConfig: (Pair<String,String>) -> Config = {
    Config().apply {
        networkConfig.join.multicastConfig.isEnabled = false
        networkConfig.join.kubernetesConfig.isEnabled = true
//        networkConfig.join.kubernetesConfig.isUsePublicIp = true
        networkConfig.join.kubernetesConfig.apply {
            setProperty("namespace", "reactive-workshop")
//            setProperty("service-name", "hazelcast-eventbus")
//            setProperty("service-port", 5701.toString())
//            setProperty("service-label-name", "hazelcast-cluster")
//            setProperty("service-label-value", "true")
            setProperty("pod-label-name", "application-type")
            setProperty("pod-label-value", "vertx")
            
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