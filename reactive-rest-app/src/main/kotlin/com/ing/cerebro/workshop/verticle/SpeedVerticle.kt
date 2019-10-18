package com.ing.cerebro.workshop.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router

class SpeedVerticle : AbstractVerticle() {
    private val router: Router by lazy { Router.router(vertx) }
    override fun start() {
//    override suspend fun start() {
        /*router.route("/").apply {
            produces(ContentTypes.plainText)
            handler { it.response().end("hello world") }
        }*/
        val server = vertx.createHttpServer()
            server.requestHandler { request ->
                request.response().end("hello world")
            }
//            .requestHandler(router)
            .listen(8085)
    }
}