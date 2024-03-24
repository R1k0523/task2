package org.example.task1proginj.service

import org.springframework.stereotype.Service

@Service
class PrometheusMockServer(val servers: Servers) {


    fun getServerLoading() = servers.serversInfo
}

