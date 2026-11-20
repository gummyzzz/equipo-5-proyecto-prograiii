package com.example.equipo5cuidados.data

import com.example.equipo5cuidados.model.FormatoEntrada
import com.example.equipo5cuidados.model.HabitoMedicion
import com.example.equipo5cuidados.model.HabitoRutina
import com.example.equipo5cuidados.model.HabitoSeguimiento
import com.example.equipo5cuidados.model.PasoRutina
import com.example.equipo5cuidados.model.PlanCuidado
import com.example.equipo5cuidados.model.Programacion
import java.time.LocalDate
import java.time.LocalTime

/**
 * Datos de prueba del plan de hábitos (primer momento: en memoria).
 * Usa las mismas personas que DatosPersonas: 1 = Rosa, 2 = Juan.
 * Los medicamentos NO van aquí: están en DatosMedicamentos (área 3).
 */
object DatosHabitos {

    private val inicio: LocalDate = LocalDate.now().minusDays(3)

    val planes: List<PlanCuidado> = listOf(
        PlanCuidado(
            id = 1,
            personaId = 1,
            nombre = "Plan de Rosa",
            fechaInicio = inicio,
            habitos = listOf(
                HabitoMedicion(
                    id = 101,
                    titulo = "Presión arterial",
                    descripcion = "Medir sentada, después de 5 minutos de reposo.",
                    fechaInicio = inicio,
                    programacion = Programacion(horas = listOf(LocalTime.of(9, 0))),
                    tipoMedicion = "Presión sistólica",
                    unidad = "mmHg",
                    minimoValido = 60.0,
                    maximoValido = 250.0
                ),
                HabitoRutina(
                    id = 102,
                    titulo = "Caminata corta",
                    descripcion = "Siempre acompañada.",
                    fechaInicio = inicio,
                    programacion = Programacion(horas = listOf(LocalTime.of(17, 0))),
                    categoria = "Movilidad",
                    pasos = listOf(
                        PasoRutina(1, "Ponerse zapatos cómodos"),
                        PasoRutina(2, "Caminar 10 minutos con el bastón"),
                        PasoRutina(3, "Descansar y tomar agua")
                    )
                ),
                HabitoSeguimiento(
                    id = 103,
                    titulo = "Ánimo del día",
                    descripcion = "",
                    fechaInicio = inicio,
                    programacion = Programacion(horas = listOf(LocalTime.of(20, 0))),
                    pregunta = "¿Cómo se sintió hoy? (1 a 5)",
                    formato = FormatoEntrada.ESCALA,
                    escalaMin = 1,
                    escalaMax = 5
                )
            )
        ),
        PlanCuidado(
            id = 2,
            personaId = 2,
            nombre = "Plan de Juan",
            fechaInicio = inicio,
            habitos = listOf(
                HabitoRutina(
                    id = 201,
                    titulo = "Curación del brazo",
                    descripcion = "Según las indicaciones del médico.",
                    fechaInicio = inicio,
                    programacion = Programacion(horas = listOf(LocalTime.of(8, 0), LocalTime.of(20, 0))),
                    categoria = "Curaciones",
                    pasos = listOf(
                        PasoRutina(1, "Lavarse las manos"),
                        PasoRutina(2, "Limpiar la zona como indicó el médico"),
                        PasoRutina(3, "Aplicar la crema indicada"),
                        PasoRutina(4, "Cubrir con gasa limpia")
                    )
                ),
                HabitoSeguimiento(
                    id = 202,
                    titulo = "Dolor",
                    descripcion = "",
                    fechaInicio = inicio,
                    programacion = Programacion(horas = listOf(LocalTime.of(21, 0))),
                    pregunta = "¿Cuánto dolor hay? (0 a 10)",
                    formato = FormatoEntrada.ESCALA,
                    escalaMin = 0,
                    escalaMax = 10
                ),
                HabitoMedicion(
                    id = 203,
                    titulo = "Temperatura",
                    descripcion = "Con termómetro digital.",
                    fechaInicio = inicio,
                    programacion = Programacion(horas = listOf(LocalTime.of(8, 0), LocalTime.of(20, 0))),
                    tipoMedicion = "Temperatura corporal",
                    unidad = "°C",
                    minimoValido = 34.0,
                    maximoValido = 43.0
                )
            )
        )
    )

    fun planDe(personaId: Long): PlanCuidado? = planes.firstOrNull { it.personaId == personaId }
}
