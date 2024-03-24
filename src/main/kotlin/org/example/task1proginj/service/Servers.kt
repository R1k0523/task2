package org.example.task1proginj.service

import org.example.task1proginj.model.Server
import org.springframework.stereotype.Service

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