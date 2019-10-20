package com.ing.cerebro.workshop

import com.ing.cerebro.workshop.core.*
import com.ing.cerebro.workshop.verticle.RestVerticle
import com.ing.cerebro.workshop.verticle.SpeedVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import kotlin.system.exitProcess

fun main() {
    val hazelcastServiceName by lazy { System.getenv("HAZELCAST_SERVICE_NAME") }
    val hazelcastHost by lazy {
        System.getenv(
            hazelcastServiceName.replace("-", "_", false).toUpperCase() + "_SERVICE_HOST"
        )
    }
    val config by lazy {
        when (isKubeEnvironment) {
            true -> kubeConfig(hazelcastServiceName)
            false -> localConfig
        }
    }

    val vertx = when (System.getenv().containsKey("no_cluster")) {
        true -> {
            println("starting as non clustered vertx instance \uD83D\uDE03")
            val fut = Future.succeededFuture(Vertx.vertx())
            fut
        }
        false -> {
            println("starting as clustered vertx instance \uD83D\uDE03")
            val options: VertxOptions = when (isKubeEnvironment) {
                true -> createClusterManager(
                    VertxOptions(),
                    clusterConfig(config),
                    hazelcastHost
                )
                false -> createClusterManager(VertxOptions(), clusterConfig(config))
            }
            Vertx.clusteredVertx(options)
        }
    }

    vertx.setHandler {
        if (it.succeeded()) {
            it.result().deployVerticle(RestVerticle())
            it.result().deployVerticle(SpeedVerticle())
        } else {
            println("failure reason: ${it.cause()}")
            exitProcess(1)
        }
    }
}
