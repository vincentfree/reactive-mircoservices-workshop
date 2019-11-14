package com.ing.cerebro.workshop.app

import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import kotlin.system.exitProcess

fun main() {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.route("/helloworld").handler {
        it.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
            .end("helloworld!")
    }
    router.route("/hello").handler {
        it.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .end(JsonObject(mapOf("message" to "hello world!")).encodePrettily())
    }
    router.route("/hello/:name").handler {
        val name = it.pathParam("name") ?: "world"
        it.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .end(JsonObject(mapOf("message" to "hello, $name")).encodePrettily())
    }
    val server = vertx.createHttpServer().apply {
        requestHandler(router)
        exceptionHandler { err ->
            println("error: ${err.message}")
        }
    }
    val port = runCatching { System.getenv("server.port").toInt() }.getOrDefault(9009)
    server.listen(port).setHandler {
        if (it.succeeded()) {
            println("server started on $port")
        } else {
            println("Failed to start server, ${it.cause().message}")
            exitProcess(1)
        }
    }

}