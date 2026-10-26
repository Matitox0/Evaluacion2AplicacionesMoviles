package com.example.prueba2aplicacionesmoviles.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba2aplicacionesmoviles.data.repositorio.ResultadoAuth
import com.example.prueba2aplicacionesmoviles.data.repositorio.UsuarioRepositorio
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager
import com.example.prueba2aplicacionesmoviles.dominio.validacion.FuerzaPassword
import com.example.prueba2aplicacionesmoviles.dominio.validacion.Validador
import com.example.prueba2aplicacionesmoviles.ui.componentes.CampoEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CampoRegistro { NOMBRE, CORREO, PASSWORD, CONFIRMACION }

data class RegistroUiState(
    val nombre: CampoEstado = CampoEstado(),
    val correo: CampoEstado = CampoEstado(),
    val password: CampoEstado = CampoEstado(),
    val confirmacion: CampoEstado = CampoEstado(),
    val aceptaTerminos: Boolean = false,
    val errorTerminos: String? = null,
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val intentosFallidos: Int = 0,      // cada vez que sube, la UI sacude el formulario
    val registroExitoso: Boolean = false
) {
    val fuerzaPassword: FuerzaPassword get() = Validador.fuerzaPassword(password.valor)
}

class RegistroViewModel(
    private val repositorio: UsuarioRepositorio,
    private val sesion: SesionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun onNombreCambio(valor: String) {
        _estado.update {
            it.copy(nombre = it.nombre.conValor(valor, Validador::validarNombre), errorGeneral = null)
        }
    }

    fun onCorreoCambio(valor: String) {
        _estado.update {
            it.copy(correo = it.correo.conValor(valor, Validador::validarCorreo), errorGeneral = null)
        }
    }

    fun onPasswordCambio(valor: String) {
        _estado.update {
            // Si ya escribió la confirmación, se revalida para que el error aparezca o desaparezca al tiro.
            val confirmacion = if (it.confirmacion.valor.isNotEmpty()) {
                it.confirmacion.copy(error = Validador.validarConfirmacion(valor, it.confirmacion.valor).mensaje)
            } else it.confirmacion

            it.copy(
                password = it.password.conValor(valor, Validador::validarPasswordRegistro),
                confirmacion = confirmacion
            )
        }
    }

    fun onConfirmacionCambio(valor: String) {
        _estado.update { s ->
            s.copy(confirmacion = s.confirmacion.conValor(valor) {
                Validador.validarConfirmacion(s.password.valor, it)
            })
        }
    }

    fun onTerminosCambio(acepta: Boolean) {
        _estado.update {
            it.copy(
                aceptaTerminos = acepta,
                errorTerminos = if (acepta) null else it.errorTerminos
            )
        }
    }

    fun onFocoPerdido(campo: CampoRegistro) {
        _estado.update {
            when (campo) {
                CampoRegistro.NOMBRE -> it.copy(nombre = it.nombre.validarAhora(Validador::validarNombre))
                CampoRegistro.CORREO -> it.copy(correo = it.correo.validarAhora(Validador::validarCorreo))
                CampoRegistro.PASSWORD -> it.copy(password = it.password.validarAhora(Validador::validarPasswordRegistro))
                CampoRegistro.CONFIRMACION -> it.copy(confirmacion = it.confirmacion.validarAhora { c ->
                    Validador.validarConfirmacion(it.password.valor, c)
                })
            }
        }
    }

    fun registrar() {
        val actual = _estado.value
        if (actual.cargando) return

        // Valida todo de una vez y marca los campos para que se vean los errores.
        val validado = actual.copy(
            nombre = actual.nombre.validarAhora(Validador::validarNombre),
            correo = actual.correo.validarAhora(Validador::validarCorreo),
            password = actual.password.validarAhora(Validador::validarPasswordRegistro),
            confirmacion = actual.confirmacion.validarAhora {
                Validador.validarConfirmacion(actual.password.valor, it)
            },
            errorTerminos = Validador.validarTerminos(actual.aceptaTerminos).mensaje,
            errorGeneral = null
        )

        val hayErrores = listOf(validado.nombre, validado.correo, validado.password, validado.confirmacion)
            .any { it.error != null } || validado.errorTerminos != null

        if (hayErrores) {
            _estado.value = validado.copy(intentosFallidos = actual.intentosFallidos + 1)
            return
        }

        _estado.value = validado.copy(cargando = true)
        viewModelScope.launch {
            when (val r = repositorio.registrar(validado.nombre.valor, validado.correo.valor, validado.password.valor)) {
                is ResultadoAuth.Exito -> {
                    sesion.iniciar(r.usuario.id) // queda logueado apenas se registra
                    _estado.update { it.copy(cargando = false, registroExitoso = true) }
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
