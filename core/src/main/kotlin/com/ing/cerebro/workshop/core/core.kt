import io.vertx.ext.web.Router

object ContentTypes {
    const val json = "application/json"
    const val plainText = "text/plain"
    const val html = "text/html"
    const val form = "application/x-www-form-urlencoded"
}

interface RouterService {
    fun finalize(): Router
}