package com.example.equipo5cuidados.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulCuidadoNoche,
    secondary = VerdeSalviaNoche,
    tertiary = AmbarNoche
)

private val LightColorScheme = lightColorScheme(
    primary = AzulCuidado,
    onPrimary = Color.White,
    primaryContainer = AzulCuidadoClaro,
    onPrimaryContainer = AzulCuidadoOscuro,
    secondary = VerdeSalvia,
    secondaryContainer = VerdeSalviaClaro,
    onSecondaryContainer = VerdeSalviaOscuro,
    tertiary = Ambar,
    tertiaryContainer = AmbarClaro,
    error = RojoError,
    errorContainer = RojoErrorClaro,
    background = Fondo,
    surface = Fondo,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario,
    surfaceVariant = Superficie5,
    surfaceContainerLowest = Superficie1,
    surfaceContainerLow = Superficie2,
    surfaceContainer = Superficie3,
    surfaceContainerHigh = Superficie4,
    surfaceContainerHighest = Superficie5,
    outline = Borde,
    outlineVariant = BordeSuave
)

@Composable
fun Equipo5cuidadosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Falso: la app usa SIEMPRE la paleta del equipo, no los colores del fondo del teléfono.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}