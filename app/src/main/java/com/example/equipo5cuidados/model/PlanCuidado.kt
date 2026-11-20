package com.example.equipo5cuidados.model


import java.time.LocalDate

/**
 * Conjunto de hábitos de una persona cuidada.
 * personaId implementa la asociación "PersonaCuidada 1 -> 1 PlanCuidado" del diagrama:
 * así el modelo de personas no depende del modelo de hábitos.
 */
data class PlanCuidado(
    val id: Long,
    val personaId: Long,
    val nombre: String,
    val fechaInicio: LocalDate,
    val habitos: List<Habito> = emptyList()
) {
    fun agregar(h: Habito): PlanCuidado = copy(habitos = habitos + h)

    fun desactivar(habitoId: Long): PlanCuidado = copy(
        habitos = habitos.map { h ->
            if (h.id != habitoId) h
            else when (h) {
                is HabitoRutina -> h.copy(activo = false)
                is HabitoMedicion -> h.copy(activo = false)
                is HabitoSeguimiento -> h.copy(activo = false)
                else -> h // TODO: cuando exista HabitoMedicamento, agregar su caso
            }
        }
    )

    fun habitosVigentes(fecha: LocalDate): List<Habito> = habitos.filter { it.estaVigente(fecha) }
}
