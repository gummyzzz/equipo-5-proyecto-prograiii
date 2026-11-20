package com.example.equipo5cuidados.ui.habitos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.equipo5cuidados.data.DatosHabitos

import com.example.equipo5cuidados.model.TipoHabito
import com.example.equipo5cuidados.ui.theme.Equipo5cuidadosTheme
import java.time.LocalDate



/**
 * Pantalla PLAN (área 2).
 * Lista los objetos PlanCuidado y Habito (rutinas, mediciones y seguimientos)
 * de la persona cuidada elegida.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(modifier: Modifier = Modifier) {
    // Por ahora las personas salen de los planes; luego se puede usar DatosPersonas.
    val planes = DatosHabitos.planes
    var personaId by rememberSaveable { mutableLongStateOf(planes.first().personaId) }
    var filtro by rememberSaveable { mutableStateOf<TipoHabito?>(null) }

    val plan = DatosHabitos.planDe(personaId)
    val habitos = plan?.habitosVigentes(LocalDate.now())
        ?.filter { filtro == null || it.tipo() == filtro }
        .orEmpty()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Plan de cuidados")
                    Text(plan?.nombre ?: "Sin plan", style = MaterialTheme.typography.bodySmall)
                }
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { /* TODO segundo momento: formulario de nuevo hábito */ }) {
                Text("Nuevo hábito")
            }
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding).padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectorPersona(planes, personaId) { personaId = it }
            FiltroTipo(filtro) { filtro = it }

            if (habitos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay hábitos para mostrar")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp), // espacio para el botón flotante
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(habitos, key = { it.id }) { habito ->
                        HabitoCard(habito)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    Equipo5cuidadosTheme { PlanScreen() }
}
