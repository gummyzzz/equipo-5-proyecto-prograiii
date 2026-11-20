package com.example.equipo5cuidados.ui.medicamentos

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.equipo5cuidados.data.DatosMedicamentos
import com.example.equipo5cuidados.model.HabitoMedicamento
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentosScreen(
    modifier: Modifier = Modifier
) {
    val medicamentos = remember {
        mutableStateListOf<HabitoMedicamento>().apply {
            addAll(DatosMedicamentos.medicamentosIniciales)
        }
    }
    var soloStockBajo by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val listaVisible = remember(medicamentos.size, medicamentos.toList(), soloStockBajo) {
        if (soloStockBajo) {
            medicamentos.filter { med ->
                med.inventario?.estaBajo(med.cantidadPorToma) == true
            }
        } else {
            medicamentos.toList()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Medicamentos") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { soloStockBajo = !soloStockBajo }
                ) {
                    Text(if (soloStockBajo) "Mostrar todos" else "Solo stock bajo")
                }

                val cantidadStockBajo = medicamentos.count {
                    it.inventario?.estaBajo(it.cantidadPorToma) == true
                }
                Text(
                    text = "Alertas stock: $cantidadStockBajo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (cantidadStockBajo > 0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaVisible, key = { it.id }) { med ->
                    MedicamentoCard(
                        medicamento = med,
                        onRegistrarToma = {
                            val invActual = med.inventario
                            if (invActual == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("${med.nombre} no tiene inventario asignado")
                                }
                                return@MedicamentoCard
                            }

                            val dosisDisp = invActual.dosisRestantes(med.cantidadPorToma)
                            if (dosisDisp <= 0) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Sin stock suficiente de ${med.nombre}")
                                }
                                return@MedicamentoCard
                            }

                            val idx = medicamentos.indexOfFirst { it.id == med.id }
                            if (idx != -1) {
                                val nuevoInventario = invActual.descontar(med.cantidadPorToma)
                                medicamentos[idx] = med.copy(inventario = nuevoInventario)
                                val alertaExtra = if (nuevoInventario.estaBajo(med.cantidadPorToma)) {
                                    " · ¡Stock bajo!"
                                } else {
                                    ""
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Toma registrada: ${med.nombre}$alertaExtra"
                                    )
                                }
                            }
                        },
                        onReponer = {
                            val invActual = med.inventario ?: return@MedicamentoCard
                            val idx = medicamentos.indexOfFirst { it.id == med.id }
                            if (idx != -1) {
                                val cantidadReposicion = med.cantidadPorToma * 5.0
                                val nuevoInventario = invActual.reponer(cantidadReposicion)
                                medicamentos[idx] = med.copy(inventario = nuevoInventario)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Repuesto +${cantidadReposicion.toInt()} ${med.unidad} de ${med.nombre}"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicamentoCard(
    medicamento: HabitoMedicamento,
    onRegistrarToma: () -> Unit,
    onReponer: () -> Unit
) {
    val colorIndicador = remember(medicamento.colorIdentificacion) {
        parseColorHex(medicamento.colorIdentificacion)
    }
    val inventario = medicamento.inventario
    val stockBajo = inventario?.estaBajo(medicamento.cantidadPorToma) == true
    val dosisRestantes = inventario?.dosisRestantes(medicamento.cantidadPorToma) ?: 0
    val vigenteHoy = medicamento.estaVigente(LocalDate.now())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (stockBajo) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(colorIndicador)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${medicamento.nombre} · ${medicamento.dosisIndicada}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = medicamento.programacion.descripcion(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (vigenteHoy) "Vigente" else "Inactivo",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (vigenteHoy) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }

            if (medicamento.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = medicamento.descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (inventario != null) {
                Text(
                    text = "Disponible: ${inventario.cantidadDisponible} ${medicamento.unidad} ($dosisRestantes tomas) · Umbral: ${inventario.umbralDosis}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (stockBajo) FontWeight.Bold else FontWeight.Normal,
                    color = if (stockBajo) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            } else {
                Text(
                    text = "Sin inventario registrado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val avisos = medicamento.avisosPrevios()
            if (avisos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                avisos.forEach { aviso ->
                    val prefijo = if (aviso.requiereConfirmacion) "⚠️ Requiere confirmación: " else "• "
                    Text(
                        text = "$prefijo${aviso.texto}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onRegistrarToma,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Registrar toma")
                }
                if (inventario != null) {
                    OutlinedButton(
                        onClick = onReponer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reponer (+5)")
                    }
                }
            }
        }
    }
}

private fun parseColorHex(hex: String): Color {
    return try {
        val limpio = hex.removePrefix("#")
        val valor = limpio.toLong(16)
        if (limpio.length == 6) {
            Color(0xFF000000 or valor)
        } else {
            Color(valor)
        }
    } catch (_: Exception) {
        Color(0xFF2563EB)
    }
}