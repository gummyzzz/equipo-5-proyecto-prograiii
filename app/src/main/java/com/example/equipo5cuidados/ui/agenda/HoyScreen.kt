
package com.example.equipo5cuidados.ui.agenda

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.equipo5cuidados.data.DatosAgenda
import com.example.equipo5cuidados.data.DatosPersonas
import com.example.equipo5cuidados.model.EstadoEvento
import com.example.equipo5cuidados.model.EventoAgenda
import com.example.equipo5cuidados.model.FormatoEntrada
import com.example.equipo5cuidados.model.Habito
import com.example.equipo5cuidados.model.HabitoMedicion
import com.example.equipo5cuidados.model.HabitoRutina
import com.example.equipo5cuidados.model.HabitoSeguimiento
import com.example.equipo5cuidados.model.OrigenRegistro
import com.example.equipo5cuidados.model.RegistroActividad
import com.example.equipo5cuidados.model.ResultadoRegistro
import com.example.equipo5cuidados.model.ValorRegistrado
import java.time.LocalDate
import java.time.LocalDateTime
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

    var eventoParaDialogo by remember {
        mutableStateOf<EventoAgenda?>(null)
    }

    var valorEscrito by remember {
        mutableStateOf("")
    }

    var errorValor by remember {
        mutableStateOf(false)
    }

    // Registrar una actividad realizada u omitida.
    val registrarEvento:
                (EventoAgenda, ResultadoRegistro, ValorRegistrado?) -> Unit =
        { evento, resultado, valor ->

            val indice = eventos.indexOfFirst {
                it.id == evento.id
            }

            if (indice != -1) {

                val actual = eventos[indice]

                // Evita registrar una actividad dos veces.
                if (actual.registro == null) {

                    val registro = RegistroActividad(
                        id = actual.id,
                        fechaHoraReal = LocalDateTime.now(),
                        resultado = resultado,
                        origen = OrigenRegistro.CUIDADOR,
                        cuidadorId = 3L,
                        observacion = "",
                        avisoPrevioConfirmado = false,
                        valor = valor
                    )

                    eventos[indice] = actual.registrar(registro)
                }
            }
        }

    // Posponer una actividad 30 minutos.
    val posponerEvento: (EventoAgenda) -> Unit = { evento ->

        val indice = eventos.indexOfFirst {
            it.id == evento.id
        }

        if (indice != -1) {

            val actual = eventos[indice]

            if (actual.registro == null) {

                val nuevaHora = actual.horaVigente()
                    .plusMinutes(30)

                eventos[indice] = actual.posponer(nuevaHora)
            }
        }
    }

    // Registrar una actividad que no se hizo.
    val omitirEvento: (EventoAgenda) -> Unit = { evento ->

        registrarEvento(
            evento,
            ResultadoRegistro.OMITIDO,
            null
        )
    }

    // Decidir cómo registrar una actividad realizada.
    val iniciarRegistro: (EventoAgenda) -> Unit = { evento ->

        val actual = eventos.firstOrNull {
            it.id == evento.id
        }

        if (actual != null && actual.registro == null) {

            when (actual.habito) {

                is HabitoRutina -> {
                    registrarEvento(
                        actual,
                        ResultadoRegistro.REALIZADO,
                        null
                    )
                }

                is HabitoMedicion,
                is HabitoSeguimiento -> {
                    eventoParaDialogo = actual
                    valorEscrito = ""
                    errorValor = false
                }

                // Medicamentos: pendiente de integrar Área 3.
                else -> Unit
            }
        }
    }

    // Filtrar y ordenar los eventos.
    val eventosVisibles = eventos
        .filter {
            personaSeleccionada == null ||
                    it.personaId == personaSeleccionada
        }
        .sortedBy { it.horaVigente() }

    val total = eventosVisibles.size

    val hechas = eventosVisibles.count {
        it.estado == EstadoEvento.REALIZADO
    }

    val avance = if (total == 0) {
        0f
    } else {
        hechas.toFloat() / total.toFloat()
    }

    // Franjas horarias.
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

            // Barra de progreso.
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

            // Actividades del día.
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

                        TarjetaAgendaInicial(
                            evento = evento,
                            onRealizar = iniciarRegistro,
                            onPosponer = posponerEvento,
                            onOmitir = omitirEvento
                        )
                    }
                }

                if (tarde.isNotEmpty()) {

                    item {
                        EncabezadoFranja("Tarde")
                    }

                    items(tarde, key = { it.id }) { evento ->

                        TarjetaAgendaInicial(
                            evento = evento,
                            onRealizar = iniciarRegistro,
                            onPosponer = posponerEvento,
                            onOmitir = omitirEvento
                        )
                    }
                }

                if (noche.isNotEmpty()) {

                    item {
                        EncabezadoFranja("Noche")
                    }

                    items(noche, key = { it.id }) { evento ->

                        TarjetaAgendaInicial(
                            evento = evento,
                            onRealizar = iniciarRegistro,
                            onPosponer = posponerEvento,
                            onOmitir = omitirEvento
                        )
                    }
                }
            }
        }
    }

    // Diálogo del Paso 8.
    eventoParaDialogo?.let { evento ->

        AlertDialog(
            onDismissRequest = {
                eventoParaDialogo = null
            },

            title = {
                Text(evento.habito.titulo)
            },

            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    val habito = evento.habito

                    if (habito is HabitoSeguimiento) {

                        Text(
                            text = habito.pregunta,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (habito.formato == FormatoEntrada.SI_NO) {
                            Text(
                                text = "Escribe sí o no",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    val etiqueta = when (habito) {
                        is HabitoMedicion -> "Valor en ${habito.unidad}"
                        else -> "Respuesta"
                    }

                    OutlinedTextField(
                        value = valorEscrito,
                        onValueChange = {
                            valorEscrito = it
                            errorValor = false
                        },
                        label = {
                            Text(etiqueta)
                        },
                        singleLine = true
                    )

                    if (errorValor) {
                        Text(
                            text = "El valor no es válido para este registro",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        val valor = convertirValor(
                            evento.habito,
                            valorEscrito
                        )

                        if (
                            valor == null ||
                            !evento.habito.aceptaValor(valor)
                        ) {
                            errorValor = true

                        } else {

                            registrarEvento(
                                evento,
                                ResultadoRegistro.REALIZADO,
                                valor
                            )

                            eventoParaDialogo = null
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        eventoParaDialogo = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Convertir los valores introducidos.
private fun convertirValor(
    habito: Habito,
    texto: String
): ValorRegistrado? {

    val entrada = texto.trim()

    return when (habito) {

        is HabitoMedicion -> {
            entrada.replace(',', '.')
                .toDoubleOrNull()
                ?.let {
                    ValorRegistrado(numero = it)
                }
        }

        is HabitoSeguimiento -> {

            when (habito.formato) {

                FormatoEntrada.ESCALA,
                FormatoEntrada.NUMERO -> {
                    entrada.replace(',', '.')
                        .toDoubleOrNull()
                        ?.let {
                            ValorRegistrado(numero = it)
                        }
                }

                FormatoEntrada.SI_NO -> {
                    when (entrada.lowercase()) {
                        "sí", "si" -> ValorRegistrado(siNo = true)
                        "no" -> ValorRegistrado(siNo = false)
                        else -> null
                    }
                }

                FormatoEntrada.TEXTO -> {
                    ValorRegistrado(texto = entrada)
                }
            }
        }

        else -> null
    }
}

// Encabezados de las franjas horarias.
@Composable
private fun EncabezadoFranja(titulo: String) {

    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

// Tarjeta de cada actividad.
@Composable
private fun TarjetaAgendaInicial(
    evento: EventoAgenda,
    onRealizar: (EventoAgenda) -> Unit,
    onPosponer: (EventoAgenda) -> Unit,
    onOmitir: (EventoAgenda) -> Unit
) {

    val hora = evento.horaVigente().format(
        DateTimeFormatter.ofPattern("HH:mm")
    )

    val persona = DatosPersonas.buscar(evento.personaId)

    val nombrePersona = persona?.nombre
        ?: "Persona desconocida"

    val colorPerfil = persona?.personaCuidada?.colorPerfil
        ?: "#9E9E9E"

    val colorIndicador = remember(colorPerfil) {
        try {
            Color(AndroidColor.parseColor(colorPerfil))
        } catch (_: IllegalArgumentException) {
            Color(0xFF9E9E9E)
        }
    }

    val realizada = evento.estado == EstadoEvento.REALIZADO
    val omitida = evento.estado == EstadoEvento.OMITIDO
    val pospuesta = evento.estado == EstadoEvento.POSPUESTO

    val atrasada = evento.estaVencido(
        LocalDateTime.now(),
        30
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (atrasada) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = hora,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            colorIndicador,
                            CircleShape
                        )
                )

                Column {

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

            // Mostrar estado de la actividad.
            when {

                realizada -> {

                    val registro = evento.registro

                    if (registro != null) {

                        val horaReal = registro.fechaHoraReal.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )

                        val responsable = registro.cuidadorId?.let {
                            DatosPersonas.buscar(it)?.nombre
                        } ?: "Sistema"

                        Text(
                            text = "✓ Hecho a las $horaReal por $responsable",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                omitida -> {

                    Text(
                        text = "✗ No se hizo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {

                    if (pospuesta) {

                        Text(
                            text = "Pospuesto hasta las $hora",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    if (atrasada) {

                        Text(
                            text = "Atrasado",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Botones del Paso 7 y Paso 8.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Button(
                            onClick = {
                                onRealizar(evento)
                            },
                            enabled =
                                evento.habito is HabitoRutina ||
                                        evento.habito is HabitoMedicion ||
                                        evento.habito is HabitoSeguimiento,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ya lo hice")
                        }

                        OutlinedButton(
                            onClick = {
                                onPosponer(evento)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Posponer 30 min")
                        }
                    }

                    // Botón del Paso 9.
                    TextButton(
                        onClick = {
                            onOmitir(evento)
                        }
                    ) {
                        Text("No se hizo")
                    }
                }
            }
        }
    }
}
