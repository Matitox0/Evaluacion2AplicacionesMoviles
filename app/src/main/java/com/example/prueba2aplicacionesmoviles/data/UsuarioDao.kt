package com.example.prueba2aplicacionesmoviles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    // Devuelve el id generado del nuevo usuario.
    @Insert
    suspend fun insertar(usuario: Usuario): Long

    // Busca por correo; devuelve null si no existe.
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Usuario?
}