package com.example.prueba2aplicacionesmoviles.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// Una opción del menú principal.
private data class OpcionMenu(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val accion: () -> Unit
)

// Menú principal. Las acciones de navegación llegan como parámetros desde AppNavegacion.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(
    viewModel: HomeViewModel,
    onSesionInvalida: () -> Unit,
    onCargarDocumento: () -> Unit,
    onTomarFoto: () -> Unit,
    onVerDocumentos: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(estado.sesionInvalida) {
        if (estado.sesionInvalida) onSesionInvalida()
    }

    val opciones = listOf(
        OpcionMenu("Cargar documento", "Sube un PDF o imagen desde tu teléfono",
            Icons.Filled.UploadFile, onCargarDocumento),
        OpcionMenu("Tomar foto", "Digitaliza un documento con la cámara",
            Icons.Filled.CameraAlt, onTomarFoto),
        OpcionMenu("Mis documentos", "Revisa, abre o elimina lo que has guardado",
            Icons.Filled.Folder, onVerDocumentos),
        OpcionMenu("Cerrar sesión", "Sale de tu cuenta actual",
            Icons.AutoMirrored.Filled.ExitToApp) { confirmarSalida = true }
    )

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    viewModel.cerrarSesion()
                }) { Text("Salir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú principal") },
                actions = {
                    IconButton(onClick = { confirmarSalida = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (estado.cargando) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Hola, ${estado.primerNombre}",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "¿Qué quieres hacer hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            opciones.forEachIndexed { indice, opcion ->
                TarjetaMenuAnimada(opcion, retrasoMs = indice * 120)
            }
        }
    }
}

// Cada tarjeta entra deslizándose desde abajo, una después de la otra.
@Composable
private fun TarjetaMenuAnimada(opcion: OpcionMenu, retrasoMs: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400, delayMillis = retrasoMs)) +
                slideInVertically(tween(400, delayMillis = retrasoMs)) { it / 2 }
    ) {
        ElevatedCard(
            onClick = opcion.accion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            opcion.icono,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        opcion.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
    }
}
