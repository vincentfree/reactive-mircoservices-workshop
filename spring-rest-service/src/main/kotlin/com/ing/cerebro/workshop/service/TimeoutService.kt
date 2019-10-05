package com.ing.cerebro.workshop.service

import com.ing.cerebro.workshop.configuration.ClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@RequestMapping("timeout")
@RestController
class TimeoutService {
    @Autowired
    lateinit var clientConfig: ClientConfig

    @RequestMapping(path = ["","/", "{time}"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun timeout(@PathVariable(value = "time", required = false) time: String?): String {
        val client = RestTemplate()
        with(clientConfig) {
            return when (time) {
                null -> {
                    val url = "http://$client_host:$client_port/client/150"
                    val res = client.getForEntity<String>(url, ResponseEntity::class)
                    "{\"message\": \"${res.body}!\"}"
                }
                else -> {
                    val url = "http://$client_host:$client_port/client/${if (time.isNotBlank()) time else "150"}"
                    val res = client.getForEntity<String>(url, ResponseEntity::class)
                    "{\"message\": \"${res.body}!\"}"
                }
            }

        }
    }
}