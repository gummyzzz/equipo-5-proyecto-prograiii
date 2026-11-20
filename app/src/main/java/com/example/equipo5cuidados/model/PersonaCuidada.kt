package com.example.equipo5cuidados.model

data class PersonaCuidada(
    val personaId: Long,
    val colorPerfil: String,
    val notas: String,
    val consentimientoRegistrado: Boolean,
    val contactos: List<ContactoEmergencia>,
    val cuidadoPor: List<Long>
) {
    init {
        require(personaId !in cuidadoPor) {
            "Una persona no puede ser cuidadora de sí misma"
        }
    }

    fun actualizarNotas(notas: String): PersonaCuidada =
        copy(notas = notas)

    fun contactoPrincipal(): ContactoEmergencia? =
        contactos.firstOrNull { it.esPrincipal } ?: contactos.firstOrNull()
}