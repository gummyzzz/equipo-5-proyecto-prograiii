
package com.example.equipo5cuidados.data

import com.example.equipo5cuidados.model.EventoAgenda
import java.time.LocalDate

object DatosAgenda {

    fun generarHoy(): List<EventoAgenda> {

        val hoy = LocalDate.now()

        val actividades = DatosHabitos.planes.flatMap { plan ->

            plan.habitosVigentes(hoy).flatMap { habito ->

                habito.programacion.ocurrenciasEn(
                    hoy,
                    habito.fechaInicio
                ).map { fechaHora ->

                    Triple(
                        plan.personaId,
                        habito,
                        fechaHora
                    )
                }
            }
        }

        // Ordenar las actividades por hora prevista.
        val ordenadas = actividades.sortedBy { it.third }

        // Crear un evento por cada horario.
        return ordenadas.mapIndexed { indice, actividad ->

            EventoAgenda(
                id = (indice + 1).toLong(),
                personaId = actividad.first,
                habito = actividad.second,
                fechaHoraPrevista = actividad.third
            )
        }

        // Pendiente: integrar los medicamentos del área 3
        // cuando DatosMedicamentos esté disponible en main.
    }
}
