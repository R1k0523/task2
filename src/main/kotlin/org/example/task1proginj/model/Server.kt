package org.example.task1proginj.model
data class Server(
    var id: Int,
    var usedRam: Int = 0,
) {
    companion object {
        val MAX_RAM = 128
    }

    fun canUse(needRam: Int) = (usedRam + needRam) <= MAX_RAM
}