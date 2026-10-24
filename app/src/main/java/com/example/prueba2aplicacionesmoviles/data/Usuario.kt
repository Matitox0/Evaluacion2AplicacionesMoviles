package com.example.prueba2aplicacionesmoviles.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Tabla "usuarios". El correo es único: no se pueden registrar dos cuentas iguales.
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    // Nunca se guarda la contraseña en texto plano: solo su hash y la sal usada.
    val passwordHash: String,
    val salt: String
)