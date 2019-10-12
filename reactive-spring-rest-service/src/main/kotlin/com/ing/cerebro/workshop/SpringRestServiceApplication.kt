package com.ing.cerebro.workshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class SpringRestServiceApplication

fun main(args: Array<String>) {
	runApplication<SpringRestServiceApplication>(*args)
}
