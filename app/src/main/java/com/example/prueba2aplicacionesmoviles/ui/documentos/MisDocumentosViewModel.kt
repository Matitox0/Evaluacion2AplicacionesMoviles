package com.example.prueba2aplicacionesmoviles.ui.documentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba2aplicacionesmoviles.data.Documento
import com.example.prueba2aplicacionesmoviles.data.repositorio.DocumentoRepository
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MisDocumentosViewModel(
    private val repositorio: DocumentoRepository,
    sesion: SesionManager
) : ViewModel() {

    private val usuarioId = sesion.usuarioActual() ?: -1

    val documentos: StateFlow<List<Documento>> = if (usuarioId != -1) {
        repositorio.listar(usuarioId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        flowOf<List<Documento>>(emptyList()).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun eliminar(documento: Documento) {
        viewModelScope.launch {
            repositorio.eliminar(documento)
        }
    }
}
