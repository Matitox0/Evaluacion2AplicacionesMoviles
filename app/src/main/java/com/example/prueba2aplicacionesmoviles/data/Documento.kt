package com.example.prueba2aplicacionesmoviles.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "documentos". Cada documento pertenece a un usuario.
@Entity(
    tableName = "documentos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE // si se borra el usuario, se borran sus documentos
        )
    ],
    indices = [Index("usuarioId")]
)
data class Documento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,
    val uri: String,        // file:// de la copia en la carpeta privada de la app
    val tipoMime: String,
    val fechaCarga: Long    // System.currentTimeMillis()
)