package com.example.prueba2aplicacionesmoviles.dominio.validacion

// Resultado de validar un campo: si es válido, mensaje es null.
data class ResultadoValidacion(val esValido: Boolean, val mensaje: String? = null) {
    companion object {
        val OK = ResultadoValidacion(true)
        fun error(mensaje: String) = ResultadoValidacion(false, mensaje)
    }
}

// Nivel de seguridad de la contraseña, usado por el indicador visual del registro.
enum class FuerzaPassword(val etiqueta: String, val progreso: Float) {
    VACIA("", 0f),
    DEBIL("Débil", 0.33f),
    MEDIA("Media", 0.66f),
    FUERTE("Fuerte", 1f)
}

/**
 * Reglas de validación de los formularios.
 * Es Kotlin puro (no conoce Compose ni Android): la UI solo muestra lo que esto responde,
 * y se puede probar con tests unitarios.
 */
object Validador {

    const val NOMBRE_MIN = 3
    const val NOMBRE_MAX = 50
    const val PASSWORD_MIN = 8

    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val REGEX_NOMBRE = Regex("^[\\p{L} ]+$") // letras (con tildes y ñ) y espacios

    fun validarNombre(nombre: String): ResultadoValidacion {
        val n = nombre.trim()
        return when {
            n.isEmpty() -> ResultadoValidacion.error("El nombre es obligatorio")
            n.length < NOMBRE_MIN -> ResultadoValidacion.error("Debe tener al menos $NOMBRE_MIN caracteres")
            n.length > NOMBRE_MAX -> ResultadoValidacion.error("Máximo $NOMBRE_MAX caracteres")
            !REGEX_NOMBRE.matches(n) -> ResultadoValidacion.error("Solo se permiten letras y espacios")
            else -> ResultadoValidacion.OK
        }
    }

    fun validarCorreo(correo: String): ResultadoValidacion {
        val c = correo.trim()
        return when {
            c.isEmpty() -> ResultadoValidacion.error("El correo es obligatorio")
            !REGEX_CORREO.matches(c) -> ResultadoValidacion.error("Formato de correo inválido (ej: nombre@correo.cl)")
            else -> ResultadoValidacion.OK
        }
    }

    // Reglas fuertes, solo para crear la cuenta.
    fun validarPasswordRegistro(password: String): ResultadoValidacion = when {
        password.isEmpty() -> ResultadoValidacion.error("La contraseña es obligatoria")
        password.any { it.isWhitespace() } -> ResultadoValidacion.error("No puede contener espacios")
        password.length < PASSWORD_MIN -> ResultadoValidacion.error("Debe tener al menos $PASSWORD_MIN caracteres")
        password.none { it.isUpperCase() } -> ResultadoValidacion.error("Debe incluir al menos una mayúscula")
        password.none { it.isLowerCase() } -> ResultadoValidacion.error("Debe incluir al menos una minúscula")
        password.none { it.isDigit() } -> ResultadoValidacion.error("Debe incluir al menos un número")
        else -> ResultadoValidacion.OK
    }

    // En el login solo se exige que venga algo; la verificación real la hace el repositorio.
    fun validarPasswordLogin(password: String): ResultadoValidacion =
        if (password.isEmpty()) ResultadoValidacion.error("Ingresa tu contraseña")
        else ResultadoValidacion.OK

    fun validarConfirmacion(password: String, confirmacion: String): ResultadoValidacion = when {
        confirmacion.isEmpty() -> ResultadoValidacion.error("Confirma tu contraseña")
        password != confirmacion -> ResultadoValidacion.error("Las contraseñas no coinciden")
        else -> ResultadoValidacion.OK
    }

    fun validarTerminos(acepta: Boolean): ResultadoValidacion =
        if (acepta) ResultadoValidacion.OK
        else ResultadoValidacion.error("Debes aceptar los términos y condiciones")

    fun fuerzaPassword(password: String): FuerzaPassword {
        if (password.isEmpty()) return FuerzaPassword.VACIA
        val puntos = listOf(
            password.length >= PASSWORD_MIN,
            password.any { it.isUpperCase() },
            password.any { it.isLowerCase() },
            password.any { it.isDigit() },
            password.any { !it.isLetterOrDigit() }
        ).count { it }
        return when {
            puntos <= 2 -> FuerzaPassword.DEBIL
            puntos <= 4 -> FuerzaPassword.MEDIA
            else -> FuerzaPassword.FUERTE
        }
    }
}
