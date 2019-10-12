//package com.ing.cerebro.workshop.service
//
//import com.ing.cerebro.workshop.configuration.ClientConfig
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.HttpHeaders
//import org.springframework.http.MediaType
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.client.RestTemplate
//import org.springframework.web.client.getForEntity
//import org.springframework.web.reactive.function.client.WebClient
//import reactor.core.publisher.Mono
//import java.util.function.Consumer
//
//@RequestMapping("timeout")
//@RestController
//class TimeoutService {
//    @Autowired
//    lateinit var clientConfig: ClientConfig
//    val client = WebClient.create()
//
//    @GetMapping(path = ["timeout", "/timeout", "/timeout/{time}"])
//    fun timeout(@PathVariable(value = "time", required = false) time: String?): Mono<String> {
//        with(clientConfig) {
//            return when (time) {
//                null -> {
//                    val url = "http://$client_host:$client_port/client/150"
//                    client.get()
//                        .uri(url)
//                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .retrieve()
//                        .bodyToMono(String::class.java)
//                }
//                else -> {
//                    val url = "http://$client_host:$client_port/client/${if (time.isNotBlank()) time else "150"}"
//                    client.get()
//                        .uri(url)
//                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .retrieve()
//                        .bodyToMono(String::class.java)
//                }
//            }
//
//        }
//    }
//}