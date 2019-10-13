package com.ing.cerebro.workshop.service


import com.ing.cerebro.workshop.core.ContentTypes
import com.ing.cerebro.workshop.core.RouterService
import io.vertx.core.Vertx
import io.vertx.core.impl.logging.Logger
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class OrderService(private val router: Router, private val vertx: Vertx) : RouterService {
    override val logger: Logger = logger()
    override fun finalize(): Router = router.apply {
        get("/orders").produces(ContentTypes.json).handler(allOrders)
    }
    val bus = vertx.eventBus()


    private val allOrders: (RoutingContext) -> Unit = {ctx ->

    }
}