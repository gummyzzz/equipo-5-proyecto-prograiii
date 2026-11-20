
package com.example.equipo5cuidados.model

import java.time.LocalDateTime

data class EventoAgenda(
    val id: Long,
    val habito: Habito,
    val personaId: Long,
    val fechaHoraPrevista: LocalDateTime,
    val estado: EstadoEvento = EstadoEvento.PENDIENTE,
    val pospuestoHasta: LocalDateTime? = null,
    val registro: RegistroActividad? = null
) {

    fun horaVigente(): LocalDateTime {
        return pospuestoHasta ?: fechaHoraPrevista
    }

    fun posponer(hasta: LocalDateTime): EventoAgenda {
        check(registro == null) {
            "No se puede posponer una tarea registrada"
        }

        return copy(
            estado = EstadoEvento.POSPUESTO,
            pospuestoHasta = hasta
        )
    }

    fun registrar(r: RegistroActividad): EventoAgenda {
        check(registro == null) {
            "Esta tarea ya tiene un registro"
        }

        val nuevoEstado = when (r.resultado) {
            ResultadoRegistro.REALIZADO -> EstadoEvento.REALIZADO
            ResultadoRegistro.OMITIDO -> EstadoEvento.OMITIDO
        }

        return copy(
            estado = nuevoEstado,
            registro = r
        )
    }

    fun estaVencido(
        ahora: LocalDateTime,
        toleranciaMin: Int
    ): Boolean {
        return registro == null &&
                ahora.isAfter(
                    horaVigente().plusMinutes(toleranciaMin.toLong())
                )
    }
}
