
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

        val medicamentos = DatosMedicamentos.porPersona.flatMap { (personaId, medicamentosPersona) ->
            medicamentosPersona.filter { it.estaVigente(hoy) }.flatMap { medicamento ->
                medicamento.programacion.ocurrenciasEn(hoy, medicamento.fechaInicio).map { fechaHora ->
                    Triple(personaId, medicamento, fechaHora)
                }
            }
        }

        // Ordenar las actividades por hora prevista.
        val ordenadas = (actividades + medicamentos).sortedBy { it.third }

        // Crear un evento por cada horario.
        return ordenadas.mapIndexed { indice, actividad ->

            EventoAgenda(
                id = (indice + 1).toLong(),
                personaId = actividad.first,
                habito = actividad.second,
                fechaHoraPrevista = actividad.third
            )
        }
    }
}
