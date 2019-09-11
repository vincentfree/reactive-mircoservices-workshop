package com.ing.cerebro.workshop.verticle

import com.ing.cerebro.workshop.core.RetrieverConfig
import com.ing.cerebro.workshop.service.HelloService
import com.ing.cerebro.workshop.service.TimeoutService
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.core.json.obj

class RestVerticle : AbstractVerticle() {

    override fun start(startPromise: Promise<Void>) {
        val retriever: ConfigRetriever = ConfigRetriever.create(vertx, RetrieverConfig.options)
        retriever.getConfig {
            val config = it.result()
            val port = config.getInteger("server.port", 8080)
            val server: HttpServer = vertx.createHttpServer()
            val router: Router = Router.router(vertx)
            HelloService(router).finalize()
            TimeoutService(router, vertx).apply {
                listenForConfig()
                finalize()
            }
            server.requestHandler(router)
            server.listen(port)
            println("Server started on port $port")
            startPromise.complete()
        }
    }
}