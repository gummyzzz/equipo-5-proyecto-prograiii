package com.example.equipo5cuidados.ui.habitos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.equipo5cuidados.model.Habito
import com.example.equipo5cuidados.model.HabitoMedicion
import com.example.equipo5cuidados.model.HabitoRutina
import com.example.equipo5cuidados.model.HabitoSeguimiento
import com.example.equipo5cuidados.model.PlanCuidado
import com.example.equipo5cuidados.model.TipoHabito

//** Fila de chips para elegir de quién se ve el plan (un chip por cada plan). */
@Composable
fun SelectorPersona(
    planes: List<PlanCuidado>,
    seleccionadaId: Long,
    onSeleccionar: (Long) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        planes.forEach { plan ->
            FilterChip(
                selected = plan.personaId == seleccionadaId,
                onClick = { onSeleccionar(plan.personaId) },
                label = { Text(plan.nombre) }
            )
        }
    }
}

/** Fila de chips para filtrar por tipo de hábito. null = todos. */
@Composable
fun FiltroTipo(
    seleccionado: TipoHabito?,
    onSeleccionar: (TipoHabito?) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(selected = seleccionado == null, onClick = { onSeleccionar(null) }, label = { Text("Todos") })
        // Medicamentos tienen su propia pestaña, por eso no se filtran aquí.
        listOf(TipoHabito.RUTINA, TipoHabito.MEDICION, TipoHabito.SEGUIMIENTO).forEach { tipo ->
            FilterChip(
                selected = seleccionado == tipo,
                onClick = { onSeleccionar(tipo) },
                label = { Text(nombreTipo(tipo)) }
            )
        }
    }
}

/**
 * Tarjeta de un hábito. Al tocarla se despliegan los detalles.
 * El `when` muestra un detalle distinto según la subclase (polimorfismo en la interfaz).
 */
@Composable
fun HabitoCard(habito: Habito) {
    var expandida by rememberSaveable(habito.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandida = !expandida }
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row {
                Text(
                    habito.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(onClick = {}, label = { Text(nombreTipo(habito.tipo())) })
            }
            Text(habito.programacion.descripcion(), style = MaterialTheme.typography.bodyMedium)

            // Resumen corto según el tipo
            val resumen = when (habito) {
                is HabitoRutina -> "${habito.categoria} · ${habito.pasos.size} pasos"
                is HabitoMedicion -> "${habito.tipoMedicion} en ${habito.unidad}"
                is HabitoSeguimiento -> habito.pregunta
                else -> "" // HabitoMedicamento se muestra en la pestaña Medicamentos
            }
            if (resumen.isNotEmpty()) Text(resumen, style = MaterialTheme.typography.bodySmall)

            AnimatedVisibility(visible = expandida) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    if (habito.descripcion.isNotBlank()) Text(habito.descripcion)
                    when (habito) {
                        is HabitoRutina -> habito.pasosOrdenados().forEach { paso ->
                            Text("${paso.orden}. ${paso.instruccion}")
                        }
                        is HabitoMedicion -> Text(
                            "Valores aceptados: ${habito.minimoValido} a ${habito.maximoValido} ${habito.unidad}"
                        )
                        is HabitoSeguimiento -> Text("Se responde con: ${habito.formato.name.lowercase()}")
                        else -> {}
                    }
                }
            }
        }
    }
}

/** Nombre legible de cada tipo. */
fun nombreTipo(tipo: TipoHabito): String = when (tipo) {
    TipoHabito.MEDICAMENTO -> "Medicamento"
    TipoHabito.RUTINA -> "Rutina"
    TipoHabito.MEDICION -> "Medición"
    TipoHabito.SEGUIMIENTO -> "Seguimiento"
}
