package com.example.equipo5cuidados.model

data class Persona(
    val id: Long,
    val nombre: String,
    val telefono: String,
    val fotoUri: String?,
    val cuidador: Cuidador?,
    val personaCuidada: PersonaCuidada?
) {
    fun actualizarDatos(nombre: String, telefono: String): Persona =
        copy(nombre = nombre, telefono = telefono)

    fun iniciales(): String = nombre
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

    fun esCuidador(): Boolean = cuidador != null

    fun esPersonaCuidada(): Boolean = personaCuidada != null
}