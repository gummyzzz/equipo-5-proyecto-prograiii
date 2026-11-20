package com.example.equipo5cuidados.data

import com.example.equipo5cuidados.model.CondicionToma
import com.example.equipo5cuidados.model.Frecuencia
import com.example.equipo5cuidados.model.HabitoMedicamento
import com.example.equipo5cuidados.model.Inventario
import com.example.equipo5cuidados.model.Programacion
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

object DatosMedicamentos {

    val condicionesEjemplo: List<CondicionToma> = listOf(
        CondicionToma(
            id = 1L,
            texto = "Tomar con el estómago lleno",
            requiereConfirmacion = true
        ),
        CondicionToma(
            id = 2L,
            texto = "Evitar tomar con lácteos",
            requiereConfirmacion = false
        ),
        CondicionToma(
            id = 3L,
            texto = "Acompañar con abundante agua",
            requiereConfirmacion = false
        )
    )

    val medicamentosIniciales: List<HabitoMedicamento> = listOf(
        HabitoMedicamento(
            id = 301L,
            titulo = "Losartán 50 mg",
            descripcion = "Control de presión arterial",
            fechaInicio = LocalDate.now().minusDays(10),
            fechaFin = null,
            activo = true,
            programacion = Programacion(
                frecuencia = Frecuencia.DIARIA,
                horas = listOf(LocalTime.of(8, 0), LocalTime.of(20, 0))
            ),
            nombre = "Losartán",
            dosisIndicada = "50 mg",
            cantidadPorToma = 1.0,
            unidad = "tableta",
            colorIdentificacion = "#2563EB",
            inventario = Inventario(
                cantidadDisponible = 12.0,
                umbralDosis = 3
            ),
            condiciones = listOf(condicionesEjemplo[2])
        ),
        HabitoMedicamento(
            id = 302L,
            titulo = "Omeprazol 20 mg",
            descripcion = "Protección gástrica antes del desayuno",
            fechaInicio = LocalDate.now().minusDays(5),
            fechaFin = null,
            activo = true,
            programacion = Programacion(
                frecuencia = Frecuencia.DIARIA,
                horas = listOf(LocalTime.of(7, 30))
            ),
            nombre = "Omeprazol",
            dosisIndicada = "20 mg",
            cantidadPorToma = 1.0,
            unidad = "cápsula",
            colorIdentificacion = "#16A34A",
            inventario = Inventario(
                cantidadDisponible = 2.0,
                umbralDosis = 3
            ),
            condiciones = listOf(condicionesEjemplo[0])
        ),
        HabitoMedicamento(
            id = 303L,
            titulo = "Vitamina D3 1000 UI",
            descripcion = "Suplemento semanal",
            fechaInicio = LocalDate.now().minusDays(14),
            fechaFin = null,
            activo = true,
            programacion = Programacion(
                frecuencia = Frecuencia.DIAS_SEMANA,
                horas = listOf(LocalTime.of(13, 0)),
                diasSemana = setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
            ),
            nombre = "Vitamina D3",
            dosisIndicada = "1000 UI",
            cantidadPorToma = 1.0,
            unidad = "gota",
            colorIdentificacion = "#D97706",
            inventario = Inventario(
                cantidadDisponible = 8.0,
                umbralDosis = 5
            ),
            condiciones = listOf(condicionesEjemplo[1])
        )
    )

    /**
     * Mapa de medicamentos asociados al ID de cada persona para que la pantalla Hoy
     * pueda resolver los medicamentos según la persona activa.
     */
    val porPersona: Map<Long, List<HabitoMedicamento>> = mapOf(
        1L to listOf(medicamentosIniciales[0], medicamentosIniciales[1]),
        2L to listOf(medicamentosIniciales[2])
    )

    fun obtenerPorPersona(personaId: Long): List<HabitoMedicamento> {
        return porPersona[personaId].orEmpty()
    }
}