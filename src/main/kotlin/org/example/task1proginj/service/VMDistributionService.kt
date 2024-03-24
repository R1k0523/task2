package org.example.task1proginj.service

import org.example.task1proginj.model.Server
import org.springframework.stereotype.Service
import kotlin.math.abs

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
