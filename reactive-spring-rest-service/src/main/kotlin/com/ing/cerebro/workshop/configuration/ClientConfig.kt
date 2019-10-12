package com.ing.cerebro.workshop.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bean")
class ClientConfig {
    val client_host: String = System.getenv("client.host") ?: "localhost"
    val client_port: String = System.getenv("client.port") ?: "9080"
}