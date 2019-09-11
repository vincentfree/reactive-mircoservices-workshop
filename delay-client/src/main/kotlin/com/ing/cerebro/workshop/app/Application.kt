package com.ing.cerebro.workshop.app

import com.ing.cerebro.workshop.verticle.ClientVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(ClientVerticle())
}