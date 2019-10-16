package com.ing.cerebro.workshop.service


import com.ing.cerebro.workshop.core.ContentTypes
import com.ing.cerebro.workshop.core.Order
import com.ing.cerebro.workshop.core.OrderStatus
import com.ing.cerebro.workshop.core.RouterService
import io.vertx.core.Vertx
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

class OrderService(private val router: Router, private val vertx: Vertx) : RouterService {
    override val logger: Logger = logger()
    override fun finalize(): Router = router.apply {
        get("/orders").produces(ContentTypes.json).handler(allOrders)
        get("/order/:id").produces(ContentTypes.json).handler(getOrderInfo)
        get("/order/status/:id").produces(ContentTypes.json).handler(orderStatus)
        delete("/order/:id").produces(ContentTypes.json).handler(pickUpOrder)
    }

    private val bus: EventBus = vertx.eventBus()
    private val orders: MutableMap<String, Order> = mutableMapOf()

    private val allOrders: (RoutingContext) -> Unit = { ctx ->
        ctx.response().end(JsonArray(orders.values.toList()).encodePrettily())
    }

    private val orderStatus: (RoutingContext) -> Unit = { ctx ->
        val orderId = ctx.pathParam("id")
        logger.info("getting response for id: $orderId")
        ctx.response().end(orders[orderId]?.status?.name ?: "order not found")
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
        val res =
            if (order?.status == OrderStatus.HOT) json {
                orders.remove(order.id)
                obj("message" to "your ${order.type} is ready for you", "order" to order)
            }
            else json { obj("message" to "order isn't ready") }
        ctx.response().end(res.encodePrettily())
    }

    fun consumeMessages(): MessageConsumer<JsonObject> = bus.consumer<JsonObject>("orders") {
        val order = it.body().mapTo(Order::class.java)
        orders.putIfAbsent(order.id, order)
        pickOrderUp(order.id)
    }

    private fun pickOrderUp(id: String) {
        vertx.setTimer(Random.nextLong(5000, 20000)) {
            val newStatus = orders[id]?.copy(status = OrderStatus.PICKED_UP)
            newStatus?.let {
                orders[it.id] = it
                logger.info("The ${it.type.name} of $id has been picked up")
                finishOrder(it.id)
            }
        }
    }

    private fun finishOrder(id: String) {
        vertx.setTimer(Random.nextLong(8000, 20000)) {
            val newStatus = orders[id]?.copy(status = OrderStatus.HOT)
            newStatus?.let {
                orders[it.id] = it
                logger.info("The ${it.type.name} of $id is done and HOT!")
                gettingCold(it.id)
            }
        }
    }

    private fun gettingCold(id: String) {
        vertx.setTimer(Random.nextLong(30000, 60000)) {
            val newStatus = orders[id]?.copy(status = OrderStatus.COLD)
            newStatus?.let {
                orders[it.id] = it
                logger.info("The ${it.type.name} of $id has stood here too long and has gotten cold!")
                removeColdOne(it.id)
            }
        }
    }

    private fun removeColdOne(id:String) {
        vertx.setTimer(40000) {
            orders.remove(id)
        }
    }

    /*private fun test(id: String) = phaseChange(OrderPhase(id, 6000, 10000, OrderStatus.COLD))
    private fun test2(id: String) = phaseChange(OrderPhase(id, 6000, 10000, OrderStatus.COLD))

    private fun phaseChange(orderPhase: OrderPhase): Promise<OrderPhase?> {
        Future.future<OrderPhase?> { promise -> return with(orderPhase) {
            val newStatus = orders[id]?.copy(status = status)
            vertx.setTimer(Random.nextLong(from, until)) {
                newStatus?.let {
                    orders[it.id] = it
                }
            }
            newStatus
        } }
        Promise.promise<OrderPhase?>()
    }

    class OrderPhase(val id: String, val from: Long, val until: Long, val status: OrderStatus)*/
}
