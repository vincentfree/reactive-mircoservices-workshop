package com.ing.cerebro.workshop.service

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldService {
    @GetMapping(name = "/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun helloWorld(@RequestParam(name = "name", required = false, defaultValue = "world") name: String): String {
        return "{\"message\": \"Hello, $name!\"}"
    }
}