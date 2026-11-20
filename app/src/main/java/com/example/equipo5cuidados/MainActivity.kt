package com.example.equipo5cuidados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.equipo5cuidados.navigation.AppDestinations
import com.example.equipo5cuidados.ui.agenda.HoyScreen
import com.example.equipo5cuidados.ui.habitos.PlanScreen
import com.example.equipo5cuidados.ui.medicamentos.MedicamentosScreen
import com.example.equipo5cuidados.ui.personas.PersonasScreen
import com.example.equipo5cuidados.ui.theme.Equipo5cuidadosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Equipo5cuidadosTheme {
                Equipo5cuidadosApp()
            }
        }
    }
}

/**
 * Menú principal de la app. NavigationSuiteScaffold dibuja las pestañas
 * (abajo en teléfonos, al costado en tablets).
 * IMPORTANTE: este archivo no se modifica en las ramas de cada módulo.
 */
@PreviewScreenSizes
@Composable
fun Equipo5cuidadosApp() {
    // Guarda qué pestaña está elegida (empieza en Hoy).
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOY) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(painterResource(it.icon), contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        // Cada pestaña muestra la pantalla de su área.
        val modifier = Modifier.fillMaxSize()
        when (currentDestination) {
            AppDestinations.HOY -> HoyScreen(modifier)
            AppDestinations.PLAN -> PlanScreen(modifier)
            AppDestinations.MEDICAMENTOS -> MedicamentosScreen(modifier)
            AppDestinations.PERSONAS -> PersonasScreen(modifier)
        }
    }
}