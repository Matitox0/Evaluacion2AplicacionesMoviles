package com.example.prueba2aplicacionesmoviles.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentoDao {

    @Insert
    suspend fun insertar(documento: Documento)

    // Flow: la lista se actualiza sola en pantalla cuando se agrega o elimina algo.
    @Query("SELECT * FROM documentos WHERE usuarioId = :usuarioId ORDER BY fechaCarga DESC")
    fun listarPorUsuario(usuarioId: Int): Flow<List<Documento>>

    @Delete
    suspend fun eliminar(documento: Documento)
}