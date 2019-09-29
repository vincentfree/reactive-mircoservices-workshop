package com.ing.cerebro.workshop.verticle

import com.ing.cerebro.workshop.core.RetrieverConfig
import com.ing.cerebro.workshop.service.ClientService
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import java.util.*

class ClientVerticle : AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(this::class.java)
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
            logger.info("Server started on port $port")
            logger.warn("config: ${it.result().encodePrettily()}")
            startPromise.complete()
        }
    }
}