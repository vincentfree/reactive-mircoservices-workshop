package com.ing.cerebro.workshop.app

import io.vertx.core.Vertx
import io.vertx.ext.web.Router

fun main() {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.route("/helloworld").handler { it.end("helloworld!") }
    val server = vertx.createHttpServer().apply { requestHandler(router) }
    server.listen(9009).setHandler {
        if(it.failed()) {
            println("Failed to start!")
        } else {
            println("Started server on 9009 ⚙")
        }
    }
}