package com.ing.cerebro.workshop.service

import com.ing.cerebro.workshop.core.ContentTypes
import com.ing.cerebro.workshop.core.Loggable
import com.ing.cerebro.workshop.core.RetrieverConfig
import com.ing.cerebro.workshop.core.RouterService
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.impl.logging.Logger
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ResponseTimeHandler
import io.vertx.ext.web.handler.TimeoutHandler
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj


class HelloService(private val router: Router) : RouterService {
    override val logger: Logger = logger()

    override fun finalize(): Router = router.apply {
        get("/hello").produces(ContentTypes.json).handler(helloWorld)
        get("/hello/:name").produces(ContentTypes.json).handler(helloInput)
    }

    private val helloWorld: (RoutingContext) -> Unit = { context ->
        context.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
            isChunked = false
            end(JsonObject().put("message", "Hello world!").toBuffer())
        }
    }
    private val helloInput: (RoutingContext) -> Unit = {
        it.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
            .end(json { obj("message" to "Hello ${it.pathParam("name")}!") }.toBuffer())
    }
}

class TimeoutService(private val router: Router, vertx: Vertx) : RouterService {
    override val logger: Logger = logger()
    private val retriever: ConfigRetriever = ConfigRetriever.create(vertx, RetrieverConfig.options)
    private var config: JsonObject = retriever.cachedConfig
    private var host = config.getString("client.host")
    private var port = config.getInteger("client.port")
    private val clientOptions = WebClientOptions().apply {
        maxPoolSize = 2000
    }
    private val client: WebClient = WebClient.create(vertx, clientOptions)
    override fun finalize(): Router = router.apply {
        route().handler(BodyHandler.create())
        route("/timeout/*").handler(TimeoutHandler.create(5000))
        route("/timeout/*").handler(ResponseTimeHandler.create())
        get("/timeout/").produces(ContentTypes.json).handler(timeoutWithTime)
        get("/timeout/:time").produces(ContentTypes.json).handler(timeoutWithTime)
    }

    private val timeoutWithTime: (RoutingContext) -> Unit = { context ->
        val time = context.request().getParam("time") ?: "150"
        client.get(port, host, "/client/$time").`as`(BodyCodec.string())
            .send { res ->
                if (res.succeeded()) {
                    val message = when (res.result().getHeader(HttpHeaders.CONTENT_TYPE.toString())) {
                        ContentTypes.plainText -> json { obj("message" to "Hello, ${res.result().body()}!") }
                        ContentTypes.json -> res.result().bodyAsJsonObject()
                        else -> json { obj("message" to "Hello, ${res.result().body()}!") }
                    }
                    context.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
                        .setStatusCode(200)
                        .end(message.toBuffer())
                } else {
                    context.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.plainText)
                        .setStatusCode(500).end(res.cause().message)
                }
            }
    }

    fun listenForConfig(): Unit = retriever.listen {
        when (it.previousConfiguration.map.keys.containsAll(it.newConfiguration.map.keys)) {
            false -> {
                config = it.newConfiguration
                host = config.getString("client.host")
                port = config.getInteger("client.port")
            }
            true -> logger.debug("nothing new in config")
        }

    }
}