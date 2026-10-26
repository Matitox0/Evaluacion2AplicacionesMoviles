package com.example.prueba2aplicacionesmoviles.navegacion

// Nombres de las pantallas de la app. Evita escribir textos sueltos al navegar.
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "menu"
    const val MIS_DOCUMENTOS = "mis_documentos"

    // "Cargar documento" recibe un argumento opcional para abrir la cámara apenas se entra.
    const val ARG_CAMARA = "camara"
    const val CARGAR_DOCUMENTO = "cargar_documento?$ARG_CAMARA={$ARG_CAMARA}"

    fun cargarDocumento(abrirCamara: Boolean = false) = "cargar_documento?$ARG_CAMARA=$abrirCamara"
}
