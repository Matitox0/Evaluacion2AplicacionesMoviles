package com.example.prueba2aplicacionesmoviles.ui.registro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prueba2aplicacionesmoviles.dominio.validacion.FuerzaPassword
import com.example.prueba2aplicacionesmoviles.ui.componentes.BannerError
import com.example.prueba2aplicacionesmoviles.ui.componentes.BotonCarga
import com.example.prueba2aplicacionesmoviles.ui.componentes.CampoFormulario
import com.example.prueba2aplicacionesmoviles.ui.componentes.sacudir
import com.example.prueba2aplicacionesmoviles.ui.theme.AmarilloAviso
import com.example.prueba2aplicacionesmoviles.ui.theme.VerdeExito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    viewModel: RegistroViewModel,
    onVolver: () -> Unit,
    onIrALogin: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(estado.registroExitoso) {
        if (estado.registroExitoso) onRegistroExitoso()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Completa tus datos para guardar tus documentos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .sacudir(estado.intentosFallidos),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CampoFormulario(
                    estado = estado.nombre,
                    onValorCambio = viewModel::onNombreCambio,
                    etiqueta = "Nombre completo",
                    iconoInicio = Icons.Filled.Person,
                    onFocoPerdido = { viewModel.onFocoPerdido(CampoRegistro.NOMBRE) },
                    habilitado = !estado.cargando
                )
                CampoFormulario(
                    estado = estado.correo,
                    onValorCambio = viewModel::onCorreoCambio,
                    etiqueta = "Correo electrónico",
                    iconoInicio = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                    onFocoPerdido = { viewModel.onFocoPerdido(CampoRegistro.CORREO) },
                    habilitado = !estado.cargando
                )
                CampoFormulario(
                    estado = estado.password,
                    onValorCambio = viewModel::onPasswordCambio,
                    etiqueta = "Contraseña",
                    iconoInicio = Icons.Filled.Lock,
                    esPassword = true,
                    textoAyuda = "Mínimo 8 caracteres, con mayúscula, minúscula y número",
                    onFocoPerdido = { viewModel.onFocoPerdido(CampoRegistro.PASSWORD) },
                    habilitado = !estado.cargando
                )
                IndicadorFuerza(estado.fuerzaPassword)
                CampoFormulario(
                    estado = estado.confirmacion,
                    onValorCambio = viewModel::onConfirmacionCambio,
                    etiqueta = "Confirmar contraseña",
                    iconoInicio = Icons.Filled.LockReset,
                    esPassword = true,
                    imeAction = ImeAction.Done,
                    onAccionFinal = viewModel::registrar,
                    onFocoPerdido = { viewModel.onFocoPerdido(CampoRegistro.CONFIRMACION) },
                    habilitado = !estado.cargando
                )

                CasillaTerminos(
                    acepta = estado.aceptaTerminos,
                    error = estado.errorTerminos,
                    onCambio = viewModel::onTerminosCambio
                )
            }

            BannerError(estado.errorGeneral)
            Spacer(Modifier.height(16.dp))

            BotonCarga(
                texto = "Crear cuenta",
                cargando = estado.cargando,
                onClick = viewModel::registrar
            )
            TextButton(onClick = onIrALogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

// Barra animada que muestra qué tan segura es la contraseña mientras se escribe.
@Composable
private fun IndicadorFuerza(fuerza: FuerzaPassword) {
    val progreso by animateFloatAsState(
        targetValue = fuerza.progreso,
        animationSpec = tween(400),
        label = "progresoFuerza"
    )
    val color by animateColorAsState(
        targetValue = when (fuerza) {
            FuerzaPassword.DEBIL -> MaterialTheme.colorScheme.error
            FuerzaPassword.MEDIA -> AmarilloAviso
            FuerzaPassword.FUERTE -> VerdeExito
            FuerzaPassword.VACIA -> Color.Transparent
        },
        label = "colorFuerza"
    )

    AnimatedVisibility(visible = fuerza != FuerzaPassword.VACIA) {
        Column(Modifier.padding(bottom = 8.dp)) {
            LinearProgressIndicator(
                progress = { progreso },
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Seguridad: ${fuerza.etiqueta}",
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}

@Composable
private fun CasillaTerminos(
    acepta: Boolean,
    error: String?,
    onCambio: (Boolean) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = acepta,
                onCheckedChange = onCambio,
                colors = if (error != null) CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colorScheme.error
                ) else CheckboxDefaults.colors()
            )
            Text("Acepto los términos y condiciones", style = MaterialTheme.typography.bodyMedium)
        }
        AnimatedVisibility(visible = error != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(
                    Icons.Filled.Error,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    error.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
