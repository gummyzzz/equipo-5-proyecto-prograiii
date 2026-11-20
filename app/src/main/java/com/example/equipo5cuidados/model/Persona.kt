package com.example.equipo5cuidados.model

data class Persona(
    val id: Long,
    val nombre: String,
    val telefono: String,
    val fotoUri: String? = null,
    val cuidador: Cuidador? = null,
    val personaCuidada: PersonaCuidada? = null
) {
    init {
        require(cuidador != null || personaCuidada != null) {
            "La persona debe tener al menos un perfil (cuidador o persona cuidada)"
        }
    }

    fun actualizarDatos(nombre: String, telefono: String): Persona =
        copy(nombre = nombre, telefono = telefono)

    fun esCuidador(): Boolean = cuidador != null

    fun esPersonaCuidada(): Boolean = personaCuidada != null

    fun iniciales(): String = nombre
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
}