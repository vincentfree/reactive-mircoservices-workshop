package com.ing.cerebro.workshop.service

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@RequestMapping("hello")
@RestController
class HelloWorldService {
    @RequestMapping(value = ["","/", "/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloWorld(@PathVariable(name = "name", required = false) name: String?): String {
        return "{\"message\": \"Hello, ${name ?: "World"}!\"}"
    }
}