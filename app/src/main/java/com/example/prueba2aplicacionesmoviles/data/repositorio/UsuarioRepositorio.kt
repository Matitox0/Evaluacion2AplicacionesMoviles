package com.example.prueba2aplicacionesmoviles.data.repositorio

import android.database.sqlite.SQLiteConstraintException
import com.example.prueba2aplicacionesmoviles.data.Usuario
import com.example.prueba2aplicacionesmoviles.data.UsuarioDao
import com.example.prueba2aplicacionesmoviles.data.seguridad.HashPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Resultado de registrar o iniciar sesión.
sealed interface ResultadoAuth {
    data class Exito(val usuario: Usuario) : ResultadoAuth
    data class Error(val mensaje: String) : ResultadoAuth
}

// Única puerta de entrada a los datos de usuarios: los ViewModels no tocan el DAO directamente.
class UsuarioRepositorio(private val dao: UsuarioDao) {

    suspend fun registrar(nombre: String, correo: String, password: String): ResultadoAuth {
        val correoNormalizado = normalizar(correo)
        if (dao.buscarPorCorreo(correoNormalizado) != null) {
            return ResultadoAuth.Error("Ya existe una cuenta con este correo")
        }

        val sal = HashPassword.generarSal()
        val hash = withContext(Dispatchers.Default) { HashPassword.hashear(password, sal) }
        val nuevo = Usuario(
            nombre = nombre.trim(),
            correo = correoNormalizado,
            passwordHash = hash,
            salt = sal
        )

        return try {
            val id = dao.insertar(nuevo)
            ResultadoAuth.Exito(nuevo.copy(id = id.toInt()))
        } catch (e: SQLiteConstraintException) {
            ResultadoAuth.Error("Ya existe una cuenta con este correo")
        }
    }

    suspend fun iniciarSesion(correo: String, password: String): ResultadoAuth {
        val usuario = dao.buscarPorCorreo(normalizar(correo))
            ?: return ResultadoAuth.Error(CREDENCIALES_INVALIDAS)

        val coincide = withContext(Dispatchers.Default) {
            HashPassword.verificar(password, usuario.salt, usuario.passwordHash)
        }
        return if (coincide) ResultadoAuth.Exito(usuario)
        else ResultadoAuth.Error(CREDENCIALES_INVALIDAS)
    }

    suspend fun obtener(id: Int): Usuario? = dao.buscarPorId(id)

    private fun normalizar(correo: String) = correo.trim().lowercase()

    private companion object {
        // Mismo mensaje en ambos casos: no revelamos si el correo existe.
        const val CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos"
    }
}
