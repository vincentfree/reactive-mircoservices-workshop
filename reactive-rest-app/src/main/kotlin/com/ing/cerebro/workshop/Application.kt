package com.ing.cerebro.workshop

import com.ing.cerebro.workshop.verticle.RestVerticle
import io.vertx.core.Vertx

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(RestVerticle())
}