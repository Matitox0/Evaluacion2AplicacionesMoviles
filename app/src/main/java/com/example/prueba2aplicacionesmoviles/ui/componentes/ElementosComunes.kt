package com.example.prueba2aplicacionesmoviles.ui.componentes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Sacude horizontalmente el elemento cada vez que cambia "disparador".
 * Se usa para avisar que el formulario tiene errores al intentar enviarlo.
 */
@Composable
fun Modifier.sacudir(disparador: Int): Modifier {
    val desplazamiento = remember { Animatable(0f) }
    LaunchedEffect(disparador) {
        if (disparador == 0) return@LaunchedEffect
        for (x in listOf(16f, -16f, 10f, -10f, 5f, -5f, 0f)) {
            desplazamiento.animateTo(x, tween(durationMillis = 45))
        }
    }
    return this.graphicsLayer { translationX = desplazamiento.value }
}

// Aviso de error general (ej: "Correo o contraseña incorrectos") que aparece y desaparece animado.
@Composable
fun BannerError(mensaje: String?, modifier: Modifier = Modifier) {
    // Guarda el último mensaje para que se siga viendo mientras se anima la salida.
    var ultimo by remember { mutableStateOf("") }
    if (mensaje != null) ultimo = mensaje

    AnimatedVisibility(
        visible = mensaje != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.ErrorOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(ultimo, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// Botón principal que cambia su texto por un indicador de carga mientras se procesa.
@Composable
fun BotonCarga(
    texto: String,
    cargando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        AnimatedContent(
            targetState = cargando,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label = "botonCarga"
        ) { estaCargando ->
            if (estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(texto)
            }
        }
    }
}
