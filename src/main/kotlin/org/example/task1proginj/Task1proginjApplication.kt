package org.example.task1proginj

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.math.abs

@SpringBootApplication
class Task1proginjApplication

fun main(args: Array<String>) {
    runApplication<Task1proginjApplication>(*args)
}


data class Server(
    var id: Int,
    var usedRam: Int = 0,
) {
    companion object {
        val MAX_RAM = 128
    }

    fun canUse(needRam: Int) = (usedRam + needRam) <= MAX_RAM
}

@Service
class Servers() {
    val serversInfo = MutableList(10000) {
        Server(id = it)
    }

    fun tryLoadServer(id: Int, needRam: Int): Int? {
        return serversInfo.getOrNull(id)?.let {
            if (it.canUse(needRam)) {
                it.usedRam += needRam
                id
            }
            else null
        }
    }
}
@Service
class PrometheusMockServer(val servers: Servers) {


    fun getServerLoading() = servers.serversInfo
}

@Service
class VMDistributionService(val prometheusMockServer: PrometheusMockServer) {
    val MAX_NORMAL_LOAD = 100

    fun List<Server>.getAccessibleMaxUsedVms(needRam: Int) = filter { it.usedRam > MAX_NORMAL_LOAD && it.canUse(needRam) }
    fun List<Server>.getAccessibleNormalUsedVms(needRam: Int) = filter { it.usedRam <= MAX_NORMAL_LOAD && it.canUse(needRam) }
    fun List<Server>.getAccessibleUnusedVms(needRam: Int) = filter { it.usedRam == 0 && it.canUse(needRam) }

    fun getOptimalVMId(needRam: Int): Int? {
        val servers = prometheusMockServer.getServerLoading()
        val normalVms = servers.getAccessibleNormalUsedVms(needRam).sortedBy { abs(it.usedRam+needRam-MAX_NORMAL_LOAD) }
        if(normalVms.isNotEmpty())
            return normalVms.first().id
        val unused = servers.getAccessibleUnusedVms(needRam).sortedBy { abs(it.usedRam+needRam-MAX_NORMAL_LOAD) }
        if(unused.isNotEmpty())
            return unused.first().id
        val maxUsedVms = servers.getAccessibleMaxUsedVms(needRam).sortedBy { abs(it.usedRam+needRam-MAX_NORMAL_LOAD) }
        if(maxUsedVms.isNotEmpty())
            return maxUsedVms.first().id
        return null
    }
}

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
data class VMRequest(
    val id: Long,
    val size: Int,
    val task: String
)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class VMResponse(
    val result: String,
    val host_id: Int? = null,
)

@RestController
class VMController(val service: VMFormTaskService) {
    @PostMapping
    fun createTask(@RequestBody request: VMRequest) = service.createTask(request = request)?.let { id ->
        VMResponse("OK", id)
    } ?:  VMResponse("NOT_OK")
}