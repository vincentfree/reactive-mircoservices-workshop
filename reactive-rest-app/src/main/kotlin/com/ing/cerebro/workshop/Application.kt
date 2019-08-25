package com.ing.cerebro.workshop

import com.ing.cerebro.workshop.verticle.RestVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

fun main() {
    val vertx = Vertx.vertx()
//    val options = DeploymentOptions().config = json { obj(""to"") }
    val options = DeploymentOptions().apply {
        config = JsonObject().put("port", 9080).put("host","localhost")
    }
    vertx.deployVerticle(RestVerticle(),options)
}