
package com.example.equipo5cuidados.model

import java.time.LocalDateTime

data class RegistroActividad(
    val id: Long,
    val fechaHoraReal: LocalDateTime,
    val resultado: ResultadoRegistro,
    val origen: OrigenRegistro,
    val cuidadorId: Long?,
    val observacion: String,
    val avisoPrevioConfirmado: Boolean,
    val valor: ValorRegistrado?
) {

    init {
        if (origen == OrigenRegistro.CUIDADOR) {
            require(cuidadorId != null) {
                "El cuidadorId es obligatorio"
            }
        } else {
            require(cuidadorId == null) {
                "El cuidadorId debe ser nulo"
            }
        }
    }

    fun fueAtrasado(
        prevista: LocalDateTime,
        toleranciaMin: Int
    ): Boolean {
        return fechaHoraReal.isAfter(
            prevista.plusMinutes(toleranciaMin.toLong())
        )
    }
}
