package com.ing.cerebro.workshop.app

import com.ing.cerebro.workshop.core.*
import com.ing.cerebro.workshop.verticle.ClientVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import kotlin.system.exitProcess

fun main() {
    val hazelcastServiceName by lazy { System.getenv("HAZELCAST_SERVICE_NAME") }
    val hazelcastHost by lazy {
        System.getenv(
            // hazelcastServiceName.replace("-", "_", false).toUpperCase() + "_SERVICE_HOST"
            System.getenv("release")
                .replace("-", "_", false)
                .toUpperCase() + "_VERTX_DELAY_SERVICE_PORT_80_TCP_ADDR"
        )
    }
    val config = when (isKubeEnvironment) {
        true -> kubeConfig(hazelcastServiceName to hazelcastHost)
        false -> localConfig
    }

    val options: VertxOptions = when (isKubeEnvironment) {
        true -> createClusterManager(
            VertxOptions(),
            clusterConfig(config),
            hazelcastHost
        )
        false -> createClusterManager(VertxOptions(), clusterConfig(config))
    }

    Vertx.clusteredVertx(options) {
        if (it.succeeded()) {
            it.result().deployVerticle(ClientVerticle())
        } else {

            println("failure reason: ${it.cause()}")
            exitProcess(1)
        }
    }
}
