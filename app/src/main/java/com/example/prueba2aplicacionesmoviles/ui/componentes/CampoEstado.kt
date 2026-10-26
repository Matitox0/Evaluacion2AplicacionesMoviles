package com.example.prueba2aplicacionesmoviles.ui.componentes

import com.example.prueba2aplicacionesmoviles.domain.validacion.ResultadoValidacion

/**
 * Estado de un campo de formulario.
 * "tocado" evita mostrar errores antes de que el usuario haya salido del campo o intentado enviar.
 */
data class CampoEstado(
    val valor: String = "",
    val error: String? = null,
    val tocado: Boolean = false
) {
    val mostrarError: Boolean get() = tocado && error != null
    val esValido: Boolean get() = error == null && valor.isNotBlank()

    fun conValor(nuevo: String, validar: (String) -> ResultadoValidacion) =
        copy(valor = nuevo, error = validar(nuevo).mensaje)

    fun marcarTocado() = copy(tocado = true)

    fun validarAhora(validar: (String) -> ResultadoValidacion) =
        copy(error = validar(valor).mensaje, tocado = true)
}
