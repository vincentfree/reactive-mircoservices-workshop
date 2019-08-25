package com.ing.cerebro.workshop.service

import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

interface RouterService {
    fun finalize(): Router
}

class HelloService(private val router: Router) : RouterService {
    override fun finalize(): Router = router.apply {
        get("/").produces(ContentTypes.json).handler(helloWorld)
        get("/:name").produces(ContentTypes.json).handler(helloInput)
    }

    private val helloWorld: (RoutingContext) -> Unit = { context ->
        context.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
            end(JsonObject().put("message", "Hello world!").encode())
        }
    }
    private val helloInput: (RoutingContext) -> Unit = { it.response().end("Hello ${it.pathParam("name")}!") }
}

class TimeoutService(private val router: Router, val vertx: Vertx) : RouterService {
    private val client: WebClient = WebClient.create(vertx)
    private val host = vertx.orCreateContext.config().getString("host")
    private val port = vertx.orCreateContext.config().getInteger("port")
    override fun finalize(): Router = router.apply {
        get("/timeout").produces(ContentTypes.json).handler(timeout)
        get("/timeout/:time").produces(ContentTypes.json).handler(timeoutWithTime)
    }

    private val timeout: (RoutingContext) -> Unit = {
        client.get(port, host, "/client/150")
            .`as`(BodyCodec.string())
            .send { res ->
                if (res.succeeded()) it.response().apply {
                    putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
                    end(
                        json {
                            obj("message" to "Hello, ${res.result().body()}!")
                        }.encode()
                    )
                }
                else it.response().setStatusCode(400).end()
            }
    }
    private val timeoutWithTime: (RoutingContext) -> Unit = { context ->
        val time = context.request().getParam("time")
        client.get(port, host, "/client/$time").`as`(BodyCodec.string())
            .send { res ->
                val message = "Hello, ${res.result().body()}!"
                if (res.succeeded()) context.response().end(message)
                else context.response().setStatusCode(400).end()
            }
    }
}

class ClientService(private val router: Router, private val vertx: Vertx) : RouterService {
    override fun finalize(): Router = router.apply {
        get("/client/:time").produces(ContentTypes.plainText).handler(clientResult)
    }

    private val clientResult: (RoutingContext) -> Unit = { ctx ->
        val time = ctx.request().getParam("time").toLongOrNull() ?: 100
        vertx.setTimer(time) {
            ctx.response().end("it took me $time millisecond to return a result")
        }
    }
}

object ContentTypes {
    const val json = "application/json"
    const val plainText = "text/plain"
    const val html = "text/html"
    const val form = "application/x-www-form-urlencoded"
}