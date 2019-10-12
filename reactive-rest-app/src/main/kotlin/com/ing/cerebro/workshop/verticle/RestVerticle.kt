package com.ing.cerebro.workshop.verticle

import com.ing.cerebro.workshop.core.RetrieverConfig
import com.ing.cerebro.workshop.service.HelloService
import com.ing.cerebro.workshop.service.TimeoutService
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class RestVerticle : AbstractVerticle() {

    override fun start(startPromise: Promise<Void>) {
        val retriever: ConfigRetriever = ConfigRetriever.create(vertx, RetrieverConfig.options)
        retriever.getConfig {
            val config: JsonObject = it.result()
            val port: Int = config.getInteger("server.port", 8080)
            val server: HttpServer = vertx.createHttpServer()
            val router: Router = Router.router(vertx)
            HelloService(router).finalize()
            TimeoutService(router, vertx).apply {
                listenForConfig()
                finalize()
            }
            server.requestHandler(router)
            val serverFut = server.listen(port)
            serverFut.setHandler { s ->
                if(s.succeeded()) {
                    startPromise.complete()
                    logger.info("Server started on port $port")
                } else {
                    logger.error("failed to start")
                    exitProcess(1)
                }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RestVerticle::class.java)
    }
}