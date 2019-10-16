package com.ing.cerebro.workshop

import com.ing.cerebro.workshop.core.*
import com.ing.cerebro.workshop.verticle.RestVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import kotlin.system.exitProcess

fun main() {
    val hazelcastServiceName by lazy { System.getenv("HAZELCAST_SERVICE_NAME") }
    val config = when (isKubeEnvironment) {
        true -> kubeConfig.invoke(hazelcastServiceName)
        false -> localConfig
    }

    val options: VertxOptions = when (isKubeEnvironment) {
        true -> createClusterManager(
            VertxOptions(),
            clusterConfig(config),
            hazelcastServiceName
        )
        false -> createClusterManager(VertxOptions(), clusterConfig(config))
    }

    Vertx.clusteredVertx(options) {
        if (it.succeeded()) {
            it.result().deployVerticle(RestVerticle())
        } else {

            println("failure reason: ${it.cause()}")
            exitProcess(1)
        }
    }
}
