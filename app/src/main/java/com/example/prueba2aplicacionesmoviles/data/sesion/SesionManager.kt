package com.example.prueba2aplicacionesmoviles.data.sesion

import android.content.Context
import androidx.core.content.edit

// Guarda qué usuario tiene la sesión abierta, para que no tenga que loguearse cada vez que abre la app.
class SesionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun iniciar(usuarioId: Int) {
        prefs.edit { putInt(CLAVE_USUARIO, usuarioId) }
    }

    fun usuarioActual(): Int? =
        prefs.getInt(CLAVE_USUARIO, SIN_SESION).takeIf { it != SIN_SESION }

    fun haySesionActiva(): Boolean = usuarioActual() != null

    fun cerrar() {
        prefs.edit { remove(CLAVE_USUARIO) }
    }

    private companion object {
        const val CLAVE_USUARIO = "usuario_id"
        const val SIN_SESION = -1
    }
}
