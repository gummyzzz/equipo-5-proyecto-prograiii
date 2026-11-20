package com.example.equipo5cuidados.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/** Cada cuánto se repite un hábito. */
enum class Frecuencia { DIARIA, DIAS_SEMANA, CADA_N_DIAS, UNICA }

/**
 * Horario de un hábito: cada cuánto y a qué horas.
 * "08:00 y 20:00" es UNA sola programación con dos horas.
 */
data class Programacion(
    val frecuencia: Frecuencia = Frecuencia.DIARIA,
    val horas: List<LocalTime>,
    val diasSemana: Set<DayOfWeek> = emptySet(),
    val cadaNDias: Int? = null
) {
    /** Devuelve la fecha y hora de cada vez que el hábito toca en [fecha]. */
    fun ocurrenciasEn(fecha: LocalDate, inicio: LocalDate): List<LocalDateTime> {
        if (fecha.isBefore(inicio)) return emptyList()
        val tocaEseDia = when (frecuencia) {
            Frecuencia.DIARIA -> true
            Frecuencia.DIAS_SEMANA -> fecha.dayOfWeek in diasSemana
            Frecuencia.CADA_N_DIAS -> {
                val n = (cadaNDias ?: 1).coerceAtLeast(1)
                ChronoUnit.DAYS.between(inicio, fecha) % n == 0L
            }
            Frecuencia.UNICA -> fecha == inicio
        }
        return if (tocaEseDia) horas.sorted().map { fecha.atTime(it) } else emptyList()
    }

    /** Texto para mostrar en pantalla, por ejemplo "Diario · 08:00 y 20:00". */
    fun descripcion(): String {
        val textoHoras = horas.sorted().joinToString(" y ") { "%02d:%02d".format(it.hour, it.minute) }
        val textoFrecuencia = when (frecuencia) {
            Frecuencia.DIARIA -> "Diario"
            Frecuencia.DIAS_SEMANA -> "Algunos días"
            Frecuencia.CADA_N_DIAS -> "Cada ${cadaNDias ?: 1} días"
            Frecuencia.UNICA -> "Una vez"
        }
        return "$textoFrecuencia · $textoHoras"
    }
}

/** Un paso de una rutina de cuidado. El orden importa (por ejemplo, en una curación). */
data class PasoRutina(
    val orden: Int,
    val instruccion: String
)
