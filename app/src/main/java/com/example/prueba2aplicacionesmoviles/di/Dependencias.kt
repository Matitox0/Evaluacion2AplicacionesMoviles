package com.example.prueba2aplicacionesmoviles.di

import android.content.Context
import com.example.prueba2aplicacionesmoviles.data.AppDatabase
import com.example.prueba2aplicacionesmoviles.data.repositorio.UsuarioRepositorio
import com.example.prueba2aplicacionesmoviles.data.sesion.SesionManager

// Arma las dependencias en un solo lugar para que los ViewModels las reciban listas.
object Dependencias {

    fun usuarioRepositorio(context: Context) =
        UsuarioRepositorio(AppDatabase.obtener(context).usuarioDao())

    fun sesion(context: Context) = SesionManager(context)
}
