package org.example.task1proginj.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VMResponse(
    val result: String,
    val host_id: Int? = null,
)
