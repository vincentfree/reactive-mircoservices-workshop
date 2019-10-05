package com.ing.cerebro.workshop.app

import com.ing.cerebro.workshop.verticle.ClientVerticle
import io.vertx.core.Vertx

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(ClientVerticle())
}