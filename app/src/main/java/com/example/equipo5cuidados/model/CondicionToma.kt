package com.example.equipo5cuidados.model

data class CondicionToma(
    val id: Long,
    val texto: String,
    val requiereConfirmacion: Boolean
)