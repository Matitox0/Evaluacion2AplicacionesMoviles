package com.example.prueba2aplicacionesmoviles.ui.documentos

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba2aplicacionesmoviles.data.Documento
import com.example.prueba2aplicacionesmoviles.data.repositorio.DocumentoRepositorio
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MisDocumentosViewModel(
    private val repositorio: DocumentoRepositorio,
    sesion: SesionManager
) : ViewModel() {

    // null mientras Room no entrega la primera lista: así la UI distingue "cargando" de "vacío".
    val documentos: StateFlow<List<Documento>?> =
        (sesion.usuarioActual()?.let(repositorio::listar) ?: flowOf(emptyList()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun uriParaAbrir(documento: Documento): Uri? = repositorio.uriParaAbrir(documento)

    fun eliminar(documento: Documento) {
        viewModelScope.launch {
            repositorio.eliminar(documento)
        }
    }
}
