package com.example.prueba2aplicacionesmoviles.dominio.validacion

object ValidadorDocumento {

    const val NOMBRE_MIN = 3
    const val NOMBRE_MAX = 60

    fun validarNombre(nombre: String): ResultadoValidacion {
        val n = nombre.trim()
        return when {
            n.isEmpty() -> ResultadoValidacion.error("El nombre es obligatorio")
            n.length < NOMBRE_MIN -> ResultadoValidacion.error("Debe tener al menos $NOMBRE_MIN caracteres")
            n.length > NOMBRE_MAX -> ResultadoValidacion.error("Máximo $NOMBRE_MAX caracteres")
            else -> ResultadoValidacion.OK
        }
    }

    fun validarArchivo(seleccionado: Boolean): ResultadoValidacion =
        if (seleccionado) ResultadoValidacion.OK
        else ResultadoValidacion.error("Debes seleccionar un archivo")
}