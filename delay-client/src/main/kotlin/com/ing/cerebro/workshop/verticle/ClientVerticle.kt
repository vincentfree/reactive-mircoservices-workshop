package com.ing.cerebro.workshop.verticle

import com.ing.cerebro.workshop.service.ClientService
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import java.util.*

class ClientVerticle : AbstractVerticle() {
    override fun start(startPromise: Promise<Void>) {
        val retriever: ConfigRetriever = ConfigRetriever.create(vertx, RetrieverConfig.options)
        retriever.getConfig {
            val config = it.result()
            val port = config.getInteger("server.port", 8080)
            val server: HttpServer = vertx.createHttpServer()
            val router: Router = Router.router(vertx)
            ClientService(router, vertx).finalize()
            server.requestHandler(router)
            server.listen(port)
            println("Server started on port $port")
            startPromise.complete()
        }
    }
}

object RetrieverConfig {
    val options = ConfigRetrieverOptions().apply {
        val fileConfig = ConfigStoreOptions()
            .setType("file")
            .setFormat("properties")
            .setConfig(JsonObject().put("path", "application.properties"))
        val k8sConfig = ConfigStoreOptions()
            .setType("configmap")
            .setOptional(true)
            .setConfig(
                JsonObject()
                    .put("namespace", "workshop-reactive")
                    .put("name", "client-config")
            )
        addStore(fileConfig)
        addStore(k8sConfig)
    }
}

class CerebroJavaProducer<K,V> : SuperClass<K,V> {
    constructor(configs: Map<String, Any>) : super(configs)
    constructor(configs: Map<String, Any>, keySerializer: Serializer<K>, valueSerializer: Serializer<V>) : super(configs, keySerializer, valueSerializer)
    constructor(properties: Properties) : super(properties)
    constructor(properties: Properties, keySerializer:Serializer<K>, valueSerializer:Serializer<V>) : super(properties, keySerializer, valueSerializer)
}
open class SuperClass<K,V> {
    constructor(configs: Map<String, Any> ) {}
    constructor(configs: Map<String, Any>, keySerializer: Serializer<K>, valueSerializer:Serializer<V>) {}
    constructor(properties: Properties) {}
    constructor(properties: Properties, keySerializer:Serializer<K>, valueSerializer:Serializer<V>) {}
}