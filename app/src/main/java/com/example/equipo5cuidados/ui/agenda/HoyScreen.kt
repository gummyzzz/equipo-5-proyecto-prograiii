
package com.example.equipo5cuidados.ui.agenda

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.equipo5cuidados.data.DatosAgenda
import com.example.equipo5cuidados.data.DatosPersonas
import com.example.equipo5cuidados.model.EstadoEvento
import com.example.equipo5cuidados.model.EventoAgenda
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoyScreen(modifier: Modifier = Modifier) {

    val hoy = remember { LocalDate.now() }

    val fechaTexto = hoy.format(
        DateTimeFormatter.ofPattern(
            "EEEE dd/MM/yyyy",
            Locale.forLanguageTag("es-BO")
        )
    ).replaceFirstChar { it.uppercaseChar() }

    val personas = DatosPersonas.personasCuidadas()

    val eventos = remember {
        mutableStateListOf<EventoAgenda>().apply {
            addAll(DatosAgenda.generarHoy())
        }
    }

    var personaSeleccionada by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    // Filtrar por persona y ordenar por hora.
    val eventosVisibles = eventos
        .filter {
            personaSeleccionada == null ||
                    it.personaId == personaSeleccionada
        }
        .sortedBy { it.horaVigente() }

    // Calcular el avance diario.
    val total = eventosVisibles.size

    val hechas = eventosVisibles.count {
        it.estado == EstadoEvento.REALIZADO
    }

    val avance = if (total == 0) {
        0f
    } else {
        hechas.toFloat() / total.toFloat()
    }

    // Agrupar por las franjas de la guía.
    val manana = eventosVisibles.filter {
        it.horaVigente().hour < 12
    }

    val tarde = eventosVisibles.filter {
        it.horaVigente().hour in 12..18
    }

    val noche = eventosVisibles.filter {
        it.horaVigente().hour >= 19
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hoy")
                        Text(
                            text = fechaTexto,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Filtros de personas.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = personaSeleccionada == null,
                    onClick = {
                        personaSeleccionada = null
                    },
                    label = { Text("Todas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor =
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                )

                personas.forEach { persona ->

                    FilterChip(
                        selected = personaSeleccionada == persona.id,
                        onClick = {
                            personaSeleccionada = persona.id
                        },
                        label = {
                            Text(persona.nombre)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor =
                                MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }

            // Progreso de las actividades.
            Text(
                text = "$hechas de $total tareas hechas",
                style = MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = { avance },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Lista agrupada del día.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                if (eventosVisibles.isEmpty()) {
                    item {
                        Text(
                            text = "No hay tareas para hoy",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (manana.isNotEmpty()) {
                    item {
                        EncabezadoFranja("Mañana")
                    }

                    items(manana, key = { it.id }) { evento ->
                        TarjetaAgendaInicial(evento)
                    }
                }

                if (tarde.isNotEmpty()) {
                    item {
                        EncabezadoFranja("Tarde")
                    }

                    items(tarde, key = { it.id }) { evento ->
                        TarjetaAgendaInicial(evento)
                    }
                }

                if (noche.isNotEmpty()) {
                    item {
                        EncabezadoFranja("Noche")
                    }

                    items(noche, key = { it.id }) { evento ->
                        TarjetaAgendaInicial(evento)
                    }
                }
            }
        }
    }
}

@Composable
private fun EncabezadoFranja(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TarjetaAgendaInicial(
    evento: EventoAgenda
) {
    val hora = evento.horaVigente().format(
        DateTimeFormatter.ofPattern("HH:mm")
    )

    val nombrePersona = DatosPersonas.buscar(
        evento.personaId
    )?.nombre ?: "Persona desconocida"

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = hora,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = evento.habito.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = nombrePersona,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
