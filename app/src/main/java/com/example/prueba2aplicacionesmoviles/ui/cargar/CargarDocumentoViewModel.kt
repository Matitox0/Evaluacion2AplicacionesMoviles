package com.example.prueba2aplicacionesmoviles.ui.cargar

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba2aplicacionesmoviles.data.repositorio.DocumentoRepository
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager
import com.example.prueba2aplicacionesmoviles.dominio.validacion.ValidadorDocumento
import com.example.prueba2aplicacionesmoviles.ui.componentes.CampoEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CargarDocumentoUiState(
    val archivoUri: Uri? = null,
    val nombreArchivo: String = "",          // nombre original del archivo elegido
    val nombre: CampoEstado = CampoEstado(), // nombre con el que se guardará
    val errorArchivo: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val guardado: Boolean = false
)

class CargarDocumentoViewModel(
    private val repositorio: DocumentoRepository,
    private val sesion: SesionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(CargarDocumentoUiState())
    val estado: StateFlow<CargarDocumentoUiState> = _estado.asStateFlow()

    fun onArchivoSeleccionado(uri: Uri?) {
        if (uri == null) return // el usuario canceló el selector
        viewModelScope.launch {
            val original = repositorio.nombreOriginal(uri)
            _estado.update {
                // Si aún no escribió un nombre, sugerimos el del archivo sin extensión.
                val nombre = if (it.nombre.valor.isBlank())
                    it.nombre.conValor(original.substringBeforeLast('.'), ValidadorDocumento::validarNombre)
                else it.nombre
                it.copy(
                    archivoUri = uri,
                    nombreArchivo = original,
                    nombre = nombre,
                    errorArchivo = null,
                    errorGeneral = null
                )
            }
        }
    }

    fun onNombreCambio(valor: String) {
        _estado.update {
            it.copy(
                nombre = it.nombre.conValor(valor, ValidadorDocumento::validarNombre),
                errorGeneral = null
            )
        }
    }

    fun onNombreFocoPerdido() {
        _estado.update { it.copy(nombre = it.nombre.validarAhora(ValidadorDocumento::validarNombre)) }
    }

    fun guardar() {
        val actual = _estado.value
        if (actual.cargando) return

        val uri = actual.archivoUri
        val nombre = actual.nombre.validarAhora(ValidadorDocumento::validarNombre)
        val errorArchivo = ValidadorDocumento.validarArchivo(uri != null).mensaje

        if (uri == null || nombre.error != null) {
            _estado.update { it.copy(nombre = nombre, errorArchivo = errorArchivo) }
            return
        }

        val usuarioId = sesion.usuarioActual()
        if (usuarioId == null) {
            _estado.update { it.copy(errorGeneral = "Tu sesión expiró. Vuelve a iniciar sesión") }
            return
        }

        viewModelScope.launch {
            _estado.update {
                it.copy(nombre = nombre, errorArchivo = null, errorGeneral = null, cargando = true)
            }
            val ok = repositorio.guardar(usuarioId, nombre.valor, uri)
            _estado.update {
                if (ok) it.copy(cargando = false, guardado = true)
                else it.copy(
                    cargando = false,
                    errorGeneral = "No se pudo guardar el documento. Intenta de nuevo"
                )
            }
        }
    }
}