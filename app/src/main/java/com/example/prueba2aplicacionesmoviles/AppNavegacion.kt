package com.example.prueba2aplicacionesmoviles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.BIENVENIDA
    ) {
        composable(Rutas.BIENVENIDA) {
            PantallaBienvenida(
                onIrALogin = { navController.navigate(Rutas.LOGIN) },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }
        // Marcadores temporales: los reemplazamos en los pasos 4 y 5.
        composable(Rutas.LOGIN) { Marcador("Pantalla de inicio de sesión") }
        composable(Rutas.REGISTRO) { Marcador("Pantalla de registro") }
    }
}

@Composable
private fun Marcador(texto: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(texto)
    }
}