package com.ing.cerebro.workshop.app

import com.ing.cerebro.workshop.core.*
import com.ing.cerebro.workshop.verticle.ClientVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import kotlin.system.exitProcess

fun main() {
    val hazelcastServiceName by lazy { System.getenv("HAZELCAST_SERVICE_NAME") }
    val hazelcastPort by lazy { hazelcastServiceName.replace("-", "_", false).capitalize() + "_SERVICE_HOST" }
    val config = when (isKubeEnvironment) {
        true -> kubeConfig.invoke(hazelcastServiceName)
        false -> localConfig
    }

    val options: VertxOptions = when (isKubeEnvironment) {
        true -> createClusterManager(
            VertxOptions(),
            clusterConfig(config),
            hazelcastPort
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
