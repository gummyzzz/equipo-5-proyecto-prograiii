package com.example.equipo5cuidados.model

data class ContactoEmergencia(
    val id: Long,
    val nombre: String,
    val telefono: String,
    val parentesco: String,
    val esPrincipal: Boolean
)