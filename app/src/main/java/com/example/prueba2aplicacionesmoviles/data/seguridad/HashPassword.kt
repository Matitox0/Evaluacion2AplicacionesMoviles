package com.example.prueba2aplicacionesmoviles.data.seguridad

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Hash de contraseñas con PBKDF2 + sal aleatoria. La contraseña nunca se guarda en texto plano.
object HashPassword {

    private const val ALGORITMO = "PBKDF2WithHmacSHA1" // disponible desde minSdk 24
    private const val ITERACIONES = 10_000
    private const val LARGO_BITS = 256
    private const val LARGO_SAL = 16

    fun generarSal(): String {
        val sal = ByteArray(LARGO_SAL)
        SecureRandom().nextBytes(sal)
        return Base64.encodeToString(sal, Base64.NO_WRAP)
    }

    fun hashear(password: String, sal: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            Base64.decode(sal, Base64.NO_WRAP),
            ITERACIONES,
            LARGO_BITS
        )
        val hash = SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
        spec.clearPassword()
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    // Comparación en tiempo constante para no filtrar información por tiempos de respuesta.
    fun verificar(password: String, sal: String, hashGuardado: String): Boolean =
        MessageDigest.isEqual(
            hashear(password, sal).toByteArray(),
            hashGuardado.toByteArray()
        )
}
