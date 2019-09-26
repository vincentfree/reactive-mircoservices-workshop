package com.ing.cerebro.workshop.core

import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.jsonObjectOf

object ContentTypes {
    const val json = "application/json"
    const val plainText = "text/plain"
    const val html = "text/html"
    const val form = "application/x-www-form-urlencoded"
}

interface RouterService {
    fun finalize(): Router
}

object RetrieverConfig {
    val options = ConfigRetrieverOptions().apply {
        addStore(
            ConfigStoreOptions().apply {
                type = "file"
                format = "properties"
                config = jsonObjectOf("path" to "application.properties")
            })
        addStore(
            ConfigStoreOptions().apply {
                type = "configmap"
                isOptional = true
                val name = System.getenv("configName") ?: "config"
                config = jsonObjectOf("namespace" to "reactive-workshop", "name" to name)
            })
    }
}