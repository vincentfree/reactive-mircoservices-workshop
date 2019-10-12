package com.ing.cerebro.workshop.service

import com.ing.cerebro.workshop.configuration.ClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
class Services {
    @Autowired
    lateinit var clientConfig: ClientConfig
    val client = WebClient.create()

    @RequestMapping(value = ["/hello", "/hello/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hello(@PathVariable(name = "name", required = false) name: String?) =
        Mono.just(Message(name?:"World"))


    @RequestMapping(value = ["/helloworld"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun helloWorld() = Mono.just("Hello World!")

    @GetMapping(path = ["timeout", "/timeout", "/timeout/{time}"])
    fun timeout(@PathVariable(value = "time", required = false) time: String?): Mono<String> {
        with(clientConfig) {
            return when (time) {
                null -> {
                    val url = "http://$client_host:$client_port/client/150"
                    client.get()
                        .uri(url)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(String::class.java)
                }
                else -> {
                    val url = "http://$client_host:$client_port/client/${if (time.isNotBlank()) time else "150"}"
                    client.get()
                        .uri(url)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(String::class.java)
                }
            }

        }
    }
}

class Message(_message: String) {
    var message: String = _message
        get() = "Hello, $field!"
}