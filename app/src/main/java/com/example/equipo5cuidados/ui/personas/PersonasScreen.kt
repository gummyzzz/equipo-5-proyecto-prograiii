package com.example.equipo5cuidados.ui.personas

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.equipo5cuidados.data.DatosPersonas
import com.example.equipo5cuidados.model.Persona

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonasScreen(modifier: Modifier = Modifier) {
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }
    val pestanas = listOf("Personas cuidadas", "Cuidadores")
    val totalPersonas = DatosPersonas.personas.size
    val personasCuidadas = DatosPersonas.personasCuidadas()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Personas de la casa")
                        Text(
                            text = "$totalPersonas personas registradas",
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
        ) {
            TabRow(selectedTabIndex = pestanaSeleccionada) {
                pestanas.forEachIndexed { index, titulo ->
                    Tab(
                        selected = pestanaSeleccionada == index,
                        onClick = { pestanaSeleccionada = index },
                        text = { Text(text = titulo) }
                    )
                }
            }

            if (pestanaSeleccionada == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(personasCuidadas, key = { it.id }) { persona ->
                        TarjetaPersonaCuidada(persona = persona)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Cuidadores",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaPersonaCuidada(persona: Persona) {
    val cuidado = persona.personaCuidada ?: return
    val colorAvatar = colorDesdeHex(cuidado.colorPerfil)
    val nombresCuidadores = cuidado.cuidadoPor
        .mapNotNull { idCuidador -> DatosPersonas.buscar(idCuidador)?.nombre }
        .joinToString(", ")

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorAvatar),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = persona.iniciales(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = persona.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tel. ${persona.telefono}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (persona.esPersonaCuidada()) {
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "Recibe cuidados") }
                    )
                }
                if (persona.esCuidador()) {
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "Cuidador/a") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cuidado.notas,
                style = MaterialTheme.typography.bodyMedium
            )

            if (nombresCuidadores.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "La cuidan: $nombresCuidadores",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun colorDesdeHex(hex: String): Color {
    val limpio = hex.trim()
    if (limpio.isEmpty()) return Color(0xFF9E9E9E)
    return runCatching { Color(AndroidColor.parseColor(limpio)) }
        .getOrElse { Color(0xFF9E9E9E) }
}