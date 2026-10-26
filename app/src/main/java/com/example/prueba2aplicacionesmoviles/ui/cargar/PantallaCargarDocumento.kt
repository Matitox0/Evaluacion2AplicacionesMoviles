package com.example.prueba2aplicacionesmoviles.ui.cargar

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.prueba2aplicacionesmoviles.ui.componentes.BannerError
import com.example.prueba2aplicacionesmoviles.ui.componentes.BotonCarga
import com.example.prueba2aplicacionesmoviles.ui.componentes.CampoFormulario

// Tipos de archivo que dejamos elegir.
private val TIPOS_PERMITIDOS = arrayOf(
    "application/pdf",
    "image/*",
    "text/plain",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCargarDocumento(
    viewModel: CargarDocumentoViewModel,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    // Recurso nativo: selector de archivos del sistema.
    val selectorArchivos = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> viewModel.onArchivoSeleccionado(uri) }

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            Toast.makeText(context, "Documento guardado", Toast.LENGTH_SHORT).show()
            onGuardado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cargar documento") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { selectorArchivos.launch(TIPOS_PERMITIDOS) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.UploadFile, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Seleccionar archivo")
            }

            // Tarjeta con el archivo elegido; aparece con animación.
            AnimatedVisibility(
                visible = estado.archivoUri != null,
                enter = fadeIn() + expandVertically()
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = estado.nombreArchivo,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            estado.errorArchivo?.let { mensaje ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            CampoFormulario(
                estado = estado.nombre,
                onValorCambio = viewModel::onNombreCambio,
                etiqueta = "Nombre del documento",
                iconoInicio = Icons.Filled.Edit,
                onFocoPerdido = viewModel::onNombreFocoPerdido,
                imeAction = ImeAction.Done,
                onAccionFinal = viewModel::guardar
            )

            BannerError(estado.errorGeneral)
            Spacer(Modifier.height(4.dp))

            BotonCarga(
                texto = "Guardar documento",
                cargando = estado.cargando,
                onClick = viewModel::guardar
            )
        }
    }
}