package com.ing.cerebro.workshop.service

import com.ing.cerebro.workshop.core.ContentTypes
import com.ing.cerebro.workshop.core.RouterService
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.impl.logging.Logger
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ClientService(private val router: Router, private val vertx: Vertx) : RouterService {
    override val logger: Logger = logger()

    override fun finalize(): Router = router.apply {
        get("/client/:time").produces(ContentTypes.plainText).handler(clientResult)
    }

    private val clientResult: (RoutingContext) -> Unit = { ctx ->
        val time = ctx.request().getParam("time").toLongOrNull() ?: 100
        vertx.setTimer(time) {
            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE,ContentTypes.plainText)
                .end("It took me $time milliseconds to return a result!")
        }
    }
}