package com.example.prueba2aplicacionesmoviles.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prueba2aplicacionesmoviles.ui.componentes.BannerError
import com.example.prueba2aplicacionesmoviles.ui.componentes.BotonCarga
import com.example.prueba2aplicacionesmoviles.ui.componentes.CampoFormulario
import com.example.prueba2aplicacionesmoviles.ui.componentes.sacudir

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    viewModel: LoginViewModel,
    onVolver: () -> Unit,
    onIrARegistro: () -> Unit,
    onLoginExitoso: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(estado.loginExitoso) {
        if (estado.loginExitoso) onLoginExitoso()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar sesión") },
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            Text("Bienvenido de vuelta", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Ingresa con tu correo y contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .sacudir(estado.intentosFallidos),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CampoFormulario(
                    estado = estado.correo,
                    onValorCambio = viewModel::onCorreoCambio,
                    etiqueta = "Correo electrónico",
                    iconoInicio = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                    onFocoPerdido = viewModel::onCorreoFocoPerdido,
                    habilitado = !estado.cargando
                )
                CampoFormulario(
                    estado = estado.password,
                    onValorCambio = viewModel::onPasswordCambio,
                    etiqueta = "Contraseña",
                    iconoInicio = Icons.Filled.Lock,
                    esPassword = true,
                    mostrarValido = false,
                    imeAction = ImeAction.Done,
                    onAccionFinal = viewModel::iniciarSesion,
                    onFocoPerdido = viewModel::onPasswordFocoPerdido,
                    habilitado = !estado.cargando
                )
            }

            BannerError(estado.errorGeneral)
            Spacer(Modifier.height(16.dp))

            BotonCarga(
                texto = "Ingresar",
                cargando = estado.cargando,
                onClick = viewModel::iniciarSesion
            )
            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
