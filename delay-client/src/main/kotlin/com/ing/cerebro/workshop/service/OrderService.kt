package com.ing.cerebro.workshop.service


import com.ing.cerebro.workshop.core.ContentTypes
import com.ing.cerebro.workshop.core.Order
import com.ing.cerebro.workshop.core.OrderStatus
import com.ing.cerebro.workshop.core.RouterService
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.impl.logging.Logger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import kotlin.random.Random
import kotlin.random.nextLong

class OrderService(private val router: Router, private val vertx: Vertx) : RouterService {
    override val logger: Logger = logger()
    override fun finalize(): Router = router.apply {
        get("/orders").produces(ContentTypes.json).handler(allOrders)
        get("/order/:id").produces(ContentTypes.json).handler(getOrderInfo)
        get("/order/status/:id").produces(ContentTypes.json).handler(orderStatus)
        delete("/order/:id").produces(ContentTypes.json).handler(pickUpOrder)
        delete("/orders").produces(ContentTypes.plainText).handler(deleteAllOrders)
    }

    private val bus: EventBus = vertx.eventBus()
    private val orders: MutableMap<String, Order> = mutableMapOf()

    fun consumeMessages(): MessageConsumer<JsonObject> = bus.consumer<JsonObject>("orders") {
        val order = it.body().mapTo(Order::class.java)
        orders.putIfAbsent(order.id, order)
        logger.info("picked up: $order")
        pickOrderUp(order.id)
    }

    private val allOrders: (RoutingContext) -> Unit = { ctx ->
        ctx.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
            end(JsonArray(orders.values.toList()).encodePrettily())
        }
    }
    private val deleteAllOrders: (RoutingContext) -> Unit = { ctx ->
        orders.clear()
        ctx.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.plainText)
            end("Everything is gone!")
        }
    }

    private val orderStatus: (RoutingContext) -> Unit = { ctx ->
        val orderId = ctx.pathParam("id")
        logger.info("getting response for id: $orderId")
        ctx.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
            end(orders[orderId]?.status?.name ?: "order not found")
        }
    }

    private val getOrderInfo: (RoutingContext) -> Unit = { ctx ->
        val orderId = ctx.pathParam("id")
        logger.info("getting order for id: $orderId")
        val res = orders[orderId]?.let {
            JsonObject.mapFrom(it).encode()
        } ?: json { obj("message" to "order not found") }.encode()
        ctx.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
            .end(res)
    }

    private val pickUpOrder: (RoutingContext) -> Unit = { ctx ->
        val orderId = ctx.pathParam("id")
        val order = orders[orderId]
        val res = if (order?.status == OrderStatus.HOT) {
            orders.remove(order.id)
            json {
                obj("message" to "your ${order.type} is ready for you", "order" to order)
            }
        } else json { obj("message" to "order isn't ready") }
        ctx.response().apply {
            putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.json)
            end(res.encodePrettily())
        }
    }

    private fun pickOrderUp(id: String) {
        vertx.setTimer(Random.nextLong(LongRange(5000, 20000))) {
            val newStatus = orders[id]?.copy(status = OrderStatus.PICKED_UP)
            newStatus?.let {
                orders[it.id] = it
                logger.info("The ${it.type.name} of $id has been picked up")
                finishOrder(it.id)
            }
        }
    }

    private fun finishOrder(id: String) {
        vertx.setTimer(Random.nextLong(LongRange(8000, 30000))) {
            val newStatus = orders[id]?.copy(status = OrderStatus.HOT)
            newStatus?.let {
                orders[it.id] = it
                logger.info("The ${it.type.name} of $id is done and HOT!")
                gettingCold(it.id)
                publishOrder(it)
            }
        }
    }
    // Presentation code snippet
    private fun publishOrder(order:Order): EventBus = bus.publish(
        "${order.customer}-order-ready",
        JsonObject.mapFrom(order),
        DeliveryOptions().addHeader("customer", order.customer)
    )


    private fun gettingCold(id: String) {
        vertx.setTimer(Random.nextLong(LongRange(30000, 60000))) {
            val newStatus = orders[id]?.copy(status = OrderStatus.COLD)
            newStatus?.let {
                orders[it.id] = it
                logger.info("The ${it.type.name} of $id has stood here too long and has gotten cold!")
                removeColdOne(it.id)
            }
        }
    }

    private fun removeColdOne(id: String) {
        vertx.setTimer(40000) {
            orders.remove(id)
        }
    }
}
