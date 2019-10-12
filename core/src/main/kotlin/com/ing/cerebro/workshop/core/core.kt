package com.ing.cerebro.workshop.core

import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.jsonObjectOf

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