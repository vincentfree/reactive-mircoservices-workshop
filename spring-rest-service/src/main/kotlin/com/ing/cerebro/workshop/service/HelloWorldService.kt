package com.ing.cerebro.workshop.service

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@RequestMapping("")
@RestController
class HelloWorldService {
    @RequestMapping(value = ["/hello", "/hello/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hello(@PathVariable(name = "name", required = false) name: String?): String {
        return "{\"message\": \"Hello, ${name ?: "World"}!\"}"
    }
    @RequestMapping(value = ["/helloworld"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun helloWorld(): String {
        return "Hello World!"
    }
}