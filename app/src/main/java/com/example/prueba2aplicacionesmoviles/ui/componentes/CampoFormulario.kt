package com.example.prueba2aplicacionesmoviles.ui.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.prueba2aplicacionesmoviles.ui.theme.VerdeExito

/**
 * Campo de texto reutilizable para todos los formularios.
 * Solo dibuja el estado que recibe: la validación la decide el ViewModel.
 * - Ícono a la izquierda que indica qué se pide.
 * - Ícono rojo y mensaje bajo el campo si hay error; check verde si está correcto.
 */
@Composable
fun CampoFormulario(
    estado: CampoEstado,
    onValorCambio: (String) -> Unit,
    etiqueta: String,
    iconoInicio: ImageVector,
    modifier: Modifier = Modifier,
    onFocoPerdido: () -> Unit = {},
    esPassword: Boolean = false,
    mostrarValido: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAccionFinal: () -> Unit = {},
    textoAyuda: String? = null,
    habilitado: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    var tuvoFoco by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = estado.valor,
        onValueChange = onValorCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        enabled = habilitado,
        isError = estado.mostrarError,
        leadingIcon = { Icon(iconoInicio, contentDescription = null) },
        trailingIcon = {
            when {
                esPassword -> IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
                estado.mostrarError -> Icon(
                    Icons.Filled.Error,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
                mostrarValido && estado.esValido -> Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Campo correcto",
                    tint = VerdeExito
                )
            }
        },
        supportingText = {
            when {
                estado.mostrarError -> Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(estado.error.orEmpty())
                }
                textoAyuda != null -> Text(textoAyuda)
            }
        },
        visualTransformation = if (esPassword && !passwordVisible) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esPassword) KeyboardType.Password else keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = {
                focusManager.clearFocus()
                onAccionFinal()
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { foco ->
                if (tuvoFoco && !foco.isFocused) onFocoPerdido()
                tuvoFoco = foco.isFocused
            }
    )
}
