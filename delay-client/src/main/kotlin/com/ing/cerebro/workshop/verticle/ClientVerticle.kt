package com.ing.cerebro.workshop.verticle

import com.ing.cerebro.workshop.core.RetrieverConfig
import com.ing.cerebro.workshop.core.prepareJsonMapper
import com.ing.cerebro.workshop.service.ClientService
import com.ing.cerebro.workshop.service.OrderService
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Router

class ClientVerticle : AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val router: Router by lazy { Router.router(vertx) }
    private val orderService: OrderService by lazy { OrderService(router, vertx) }

    override fun start(startPromise: Promise<Void>) {
        prepareJsonMapper()
        val retriever: ConfigRetriever = ConfigRetriever.create(vertx, RetrieverConfig.options)
        retriever.getConfig {
            val config = it.result()
            val port = config.getInteger("server.port", 8080)
            val server: HttpServer = vertx.createHttpServer()
            ClientService(router, vertx).finalize()
            orderService.apply {
                consumeMessages()
                finalize()
            }
            server.requestHandler(router)
            server.listen(port)
            logger.info("Server started on port $port ⚙️")
            startPromise.complete()
        }
    }

    override fun stop(stopPromise: Promise<Void>) {
        orderService.consumeMessages().unregister {
            if (it.succeeded()) {
                logger.info("Successfully stopped listening to event bus")
                stopPromise.complete()
            } else {
                logger.error("Failed to unregister from event bus")
                stopPromise.fail(it.cause())
            }
        }
    }
}