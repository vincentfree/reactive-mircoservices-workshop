package com.ing.cerebro.workshop.core

import com.hazelcast.config.Config
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.VertxOptions
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
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

object Hazelcast {
    fun clusterConfig(hazelcastConfig: Config): ClusterManager = HazelcastClusterManager(hazelcastConfig)
    fun setClusterManager(mgr:ClusterManager, options: VertxOptions): VertxOptions = options.setClusterManager(mgr)
}