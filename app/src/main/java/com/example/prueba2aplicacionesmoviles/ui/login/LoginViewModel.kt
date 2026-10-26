package com.example.prueba2aplicacionesmoviles.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba2aplicacionesmoviles.data.repositorio.ResultadoAuth
import com.example.prueba2aplicacionesmoviles.data.repositorio.UsuarioRepositorio
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager
import com.example.prueba2aplicacionesmoviles.domain.validacion.Validador
import com.example.prueba2aplicacionesmoviles.ui.componentes.CampoEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: CampoEstado = CampoEstado(),
    val password: CampoEstado = CampoEstado(),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val intentosFallidos: Int = 0,
    val loginExitoso: Boolean = false
)

class LoginViewModel(
    private val repositorio: UsuarioRepositorio,
    private val sesion: SesionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onCorreoCambio(valor: String) {
        _estado.update {
            it.copy(correo = it.correo.conValor(valor, Validador::validarCorreo), errorGeneral = null)
        }
    }

    fun onPasswordCambio(valor: String) {
        _estado.update {
            it.copy(password = it.password.conValor(valor, Validador::validarPasswordLogin), errorGeneral = null)
        }
    }

    fun onCorreoFocoPerdido() {
        _estado.update { it.copy(correo = it.correo.validarAhora(Validador::validarCorreo)) }
    }

    fun onPasswordFocoPerdido() {
        _estado.update { it.copy(password = it.password.validarAhora(Validador::validarPasswordLogin)) }
    }

    fun iniciarSesion() {
        val actual = _estado.value
        if (actual.cargando) return

        val validado = actual.copy(
            correo = actual.correo.validarAhora(Validador::validarCorreo),
            password = actual.password.validarAhora(Validador::validarPasswordLogin),
            errorGeneral = null
        )

        if (validado.correo.error != null || validado.password.error != null) {
            _estado.value = validado.copy(intentosFallidos = actual.intentosFallidos + 1)
            return
        }

        _estado.value = validado.copy(cargando = true)
        viewModelScope.launch {
            when (val r = repositorio.iniciarSesion(validado.correo.valor, validado.password.valor)) {
                is ResultadoAuth.Exito -> {
                    sesion.iniciar(r.usuario.id)
                    _estado.update { it.copy(cargando = false, loginExitoso = true) }
                }
                is ResultadoAuth.Error -> _estado.update {
                    it.copy(
                        cargando = false,
                        errorGeneral = r.mensaje,
                        intentosFallidos = it.intentosFallidos + 1
                    )
                }
            }
        }
    }
}
