package com.ing.cerebro.workshop.verticle

import com.ing.cerebro.workshop.core.Loggable
import com.ing.cerebro.workshop.core.RetrieverConfig
import com.ing.cerebro.workshop.core.prepareJsonMapper
import com.ing.cerebro.workshop.service.HelloService
import com.ing.cerebro.workshop.service.TimeoutService
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.impl.logging.Logger
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import kotlin.system.exitProcess

class RestVerticle : AbstractVerticle(), Loggable {
    private val router: Router by lazy { Router.router(vertx) }
    private val helloService by lazy { HelloService(router, vertx) }
    override val logger: Logger = logger()
    override fun start(startPromise: Promise<Void>) {
        val retriever: ConfigRetriever = ConfigRetriever.create(vertx, RetrieverConfig.options)
        retriever.getConfig {
            prepareJsonMapper()
            val config: JsonObject = it.result()
            val port: Int = config.getInteger("server.port", 8080)
            // Native transport on BSD
//            val server: HttpServer = vertx.createHttpServer(HttpServerOptions().setReusePort(true))
            val server: HttpServer = vertx.createHttpServer()
            helloService.apply {
                consumeMessages()
                finalize()
            }
            TimeoutService(router, vertx).apply {
                listenForConfig()
                finalize()
            }
            server.requestHandler(router)
            val serverFut = server.listen(port)
            serverFut.setHandler { s ->
                if (s.succeeded()) {
                    startPromise.complete()
                    logger.info("Server started on port $port ⚙️")
                    logger.info("Clustered state: ${vertx.isClustered}")
                } else {
                    logger.error("failed to start, reason: ${s.cause().message}")
                    exitProcess(1)
                }
            }
        }
    }

    override fun stop(stopPromise: Promise<Void>) {
        helloService.consumeMessages().unregister {
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