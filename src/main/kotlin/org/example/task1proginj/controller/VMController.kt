package org.example.task1proginj.controller

import org.example.task1proginj.service.VMFormTaskService
import org.example.task1proginj.model.VMRequest
import org.example.task1proginj.model.VMResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VMController(val service: VMFormTaskService) {
    @PostMapping
    fun createTask(@RequestBody request: VMRequest) = service.createTask(request = request)?.let { id ->
        VMResponse("OK", id)
    } ?:  VMResponse("NOT_OK")
}