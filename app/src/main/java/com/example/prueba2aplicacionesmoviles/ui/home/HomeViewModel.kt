package com.example.prueba2aplicacionesmoviles.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba2aplicacionesmoviles.data.repositorio.UsuarioRepositorio
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val nombreUsuario: String = "",
    val cargando: Boolean = true,
    val sesionInvalida: Boolean = false
) {
    // Solo el primer nombre, para un saludo más cercano.
    val primerNombre: String get() = nombreUsuario.trim().substringBefore(" ")
}

class HomeViewModel(
    private val repositorio: UsuarioRepositorio,
    private val sesion: SesionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(HomeUiState())
    val estado: StateFlow<HomeUiState> = _estado.asStateFlow()

    init {
        cargarUsuario()
    }

    private fun cargarUsuario() {
        viewModelScope.launch {
            val usuario = sesion.usuarioActual()?.let { repositorio.obtener(it) }
            if (usuario == null) {
                // La sesión apunta a un usuario que ya no existe: se limpia y se vuelve al inicio.
                sesion.cerrar()
                _estado.update { it.copy(cargando = false, sesionInvalida = true) }
            } else {
                _estado.update { it.copy(nombreUsuario = usuario.nombre, cargando = false) }
            }
        }
    }
}
