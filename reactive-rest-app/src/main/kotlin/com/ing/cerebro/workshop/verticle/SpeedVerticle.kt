package com.ing.cerebro.workshop.verticle

import io.vertx.core.AbstractVerticle

class SpeedVerticle : AbstractVerticle() {
    override fun start() {
        val server = vertx.createHttpServer()
        server.requestHandler { request ->
            request.response().end("hello world")
        }.listen(8085)
    }
}