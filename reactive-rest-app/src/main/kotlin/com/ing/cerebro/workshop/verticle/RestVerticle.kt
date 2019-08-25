package com.ing.cerebro.workshop.verticle

import com.ing.cerebro.workshop.service.ClientService
import com.ing.cerebro.workshop.service.HelloService
import com.ing.cerebro.workshop.service.TimeoutService
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router

class RestVerticle : AbstractVerticle() {

    override fun start(startPromise: Promise<Void>) {
        val port = config().getInteger("port",8080)
        val server: HttpServer = vertx.createHttpServer()
        val router: Router = Router.router(vertx)
        HelloService(router).finalize()
        TimeoutService(router,vertx).finalize()
        ClientService(router, vertx).finalize()
        server.requestHandler(router)
        server.listen(port)
        println("Server started on port $port")
        startPromise.complete()
    }
}