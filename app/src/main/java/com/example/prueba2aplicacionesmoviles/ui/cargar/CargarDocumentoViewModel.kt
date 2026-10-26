package com.example.prueba2aplicacionesmoviles.ui.cargar

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba2aplicacionesmoviles.data.repositorio.DocumentoRepositorio
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager
import com.example.prueba2aplicacionesmoviles.dominio.validacion.ValidadorDocumento
import com.example.prueba2aplicacionesmoviles.ui.componentes.CampoEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// De dónde viene el archivo elegido; la UI muestra un ícono distinto en cada caso.
enum class OrigenArchivo { SELECTOR, CAMARA }

data class CargarDocumentoUiState(
    val archivoUri: Uri? = null,
    val nombreArchivo: String = "",          // nombre original del archivo elegido
    val origen: OrigenArchivo? = null,
    val nombre: CampoEstado = CampoEstado(), // nombre con el que se guardará
    val errorArchivo: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val guardado: Boolean = false
)

class CargarDocumentoViewModel(
    private val repositorio: DocumentoRepositorio,
    private val sesion: SesionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(CargarDocumentoUiState())
    val estado: StateFlow<CargarDocumentoUiState> = _estado.asStateFlow()

    // Uri donde la cámara está escribiendo. Vive en el ViewModel para sobrevivir a la rotación
    // de pantalla que muchas apps de cámara provocan al volver.
    private var fotoPendiente: Uri? = null

    fun onArchivoSeleccionado(uri: Uri?) {
        if (uri == null) return // el usuario canceló el selector
        viewModelScope.launch {
            val original = repositorio.nombreOriginal(uri)
            aplicarArchivo(uri, original, OrigenArchivo.SELECTOR, original.substringBeforeLast('.'))
        }
    }

    // Prepara el archivo temporal para la cámara. Devuelve null si no se pudo crear.
    fun prepararFoto(): Uri? = try {
        repositorio.crearUriFoto().also { fotoPendiente = it }
    } catch (e: Exception) {
        _estado.update { it.copy(errorGeneral = "No se pudo preparar la cámara") }
        null
    }

    fun onCamaraNoDisponible() {
        fotoPendiente = null
        _estado.update { it.copy(errorGeneral = "Este dispositivo no tiene una cámara disponible") }
    }

    fun onFotoTomada(exito: Boolean) {
        val uri = fotoPendiente ?: return
        fotoPendiente = null
        if (!exito) return // el usuario cerró la cámara sin tomar la foto
        val fecha = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        aplicarArchivo(uri, "Foto tomada con la cámara", OrigenArchivo.CAMARA, "Foto $fecha")
    }

    private fun aplicarArchivo(uri: Uri, original: String, origen: OrigenArchivo, nombreSugerido: String) {
        _estado.update {
            // Si aún no escribió un nombre, sugerimos uno.
            val nombre = if (it.nombre.valor.isBlank())
                it.nombre.conValor(nombreSugerido, ValidadorDocumento::validarNombre)
            else it.nombre
            it.copy(
                archivoUri = uri,
                nombreArchivo = original,
                origen = origen,
                nombre = nombre,
                errorArchivo = null,
                errorGeneral = null
            )
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

    // Al salir de la pantalla (guardando o no) se borran las fotos temporales de la cámara.
    override fun onCleared() {
        repositorio.limpiarFotosTemporales()
    }
}
