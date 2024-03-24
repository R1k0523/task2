package org.example.task1proginj.service

import org.example.task1proginj.model.VMRequest
import org.springframework.stereotype.Service

@Service
class VMFormTaskService(val service: VMDistributionService, val servers: Servers) {
    fun createTask(request: VMRequest, retries: Int = 0) : Int? {
        val vm = service.getOptimalVMId(request.size)
        val vmId = vm?.let {
            servers.tryLoadServer(it, request.size)
        }
        return if (vmId == null && retries > 3) {
            createTask(request,  retries = retries+1)
        } else vmId
    }

}