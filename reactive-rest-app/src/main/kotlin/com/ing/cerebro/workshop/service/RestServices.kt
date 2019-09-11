package com.ing.cerebro.workshop.service

import com.ing.cerebro.workshop.core.ContentTypes
import com.ing.cerebro.workshop.core.RouterService
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj


class HelloService(private val router: Router) : RouterService {
    override fun finalize(): Router = router.apply {
        get("/").produces(ContentTypes.json).handler(helloWorld)
        get("/").produces(ContentTypes.json).handler({it.response()})
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

class TimeoutService(private val router: Router, private val vertx: Vertx) : RouterService {

    private val retriever: ConfigRetriever = ConfigRetriever.create(vertx)
    var config: JsonObject = retriever.cachedConfig
    private val client: WebClient = WebClient.create(vertx)
    override fun finalize(): Router = router.apply {
        get("/timeout").produces(ContentTypes.json).handler(timeout)
        get("/timeout/:time").produces(ContentTypes.json).handler(timeoutWithTime)
    }

    private val timeout: (RoutingContext) -> Unit = {
        client.get(config.getInteger("client.port"), config.getString("client.host"), "/client/150")
            .`as`(BodyCodec.string())
            .send { res ->
                if (res.succeeded()) it.response().apply {
                    println("client port: ${config.getInteger("client.port")}")
                    println("client port:")
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
        client.get(config.getInteger("port"), config.getString("host"), "/client/$time").`as`(BodyCodec.string())
            .send { res ->
                if (res.succeeded()) {
                    val message = when (res.result().getHeader(HttpHeaders.CONTENT_TYPE.toString())) {
                        ContentTypes.plainText -> "Hello, ${res.result().bodyAsString()}!"
                        ContentTypes.json -> res.result().bodyAsJsonObject().encodePrettily()
                        else -> "Hello, ${res.result().body()}!"
                    }
                    context.response().end(message)
                } else {
                    context.response().setStatusCode(500).end(res.cause().message)
                }
            }
    }

    private fun getAppConfig() {
        retriever.getConfig {
            config = it.result()
        }
    }

    fun listenForConfig(): Unit = retriever.listen { config = it.newConfiguration }
}