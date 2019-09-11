import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

interface RouterService {
    fun finalize(): Router
}

class ClientService(private val router: Router, private val vertx: Vertx) : RouterService {
    override fun finalize(): Router = router.apply {
        get("/client/:time").produces(ContentTypes.plainText).handler(clientResult)
    }

    private val clientResult: (RoutingContext) -> Unit = { ctx ->
        val time = ctx.request().getParam("time").toLongOrNull() ?: 100
        vertx.setTimer(time) {
            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE,ContentTypes.plainText)
                .end("it took me $time millisecond to return a result")
        }
    }
}