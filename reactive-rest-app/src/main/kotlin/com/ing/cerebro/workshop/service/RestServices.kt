package com.ing.cerebro.workshop.service

import com.ing.cerebro.workshop.core.*
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
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
import java.util.*
import kotlin.random.Random


class HelloService(private val router: Router, vertx: Vertx) : RouterService {
    override val logger: Logger = logger()
    private val bus: EventBus = vertx.eventBus()

    override fun finalize(): Router = router.apply {
        get("/hello/").produces(ContentTypes.json).handler(helloWorld)
        get("/helloworld").produces(ContentTypes.plainText).handler(helloPlainWorld)
        get("/hello/:name").produces(ContentTypes.json).handler(helloInput)
        put("/order").produces(ContentTypes.plainText).handler(processOrder)
    }


    private val helloPlainWorld: (RoutingContext) -> Unit = { context ->
        context.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.plainText)
            isChunked = false
            end("Hello world!")
        }
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

    private val processOrder: (RoutingContext) -> Unit = {
        val uuid: UUID = UUID.randomUUID()
        val result = randomOrder(uuid)
        publishOrder(result)
        it.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.plainText)
            isChunked = false
            end(uuid.toString())
        }
    }

    private fun publishOrder(order: Order) {
        bus.publish("orders", JsonObject.mapFrom(order))
    }

    private fun randomOrder(uuid: UUID): Order = Order(uuid.toString(), randomType(), OrderStatus.PENDING)
    private fun randomType(): OrderType = when (Random.nextInt(0, 5)) {
        0 -> OrderType.COFFEE
        1 -> OrderType.TEA
        2 -> OrderType.LATTE
        3 -> OrderType.LATTE
        4 -> OrderType.CHOCOLATE_MILK
        5 -> OrderType.MILK
        else -> throw IllegalArgumentException("Something went wrong with random")
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