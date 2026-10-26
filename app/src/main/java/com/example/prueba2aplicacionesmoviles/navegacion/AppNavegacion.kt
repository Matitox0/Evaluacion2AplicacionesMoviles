package com.example.prueba2aplicacionesmoviles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prueba2aplicacionesmoviles.data.AppDatabase
import com.example.prueba2aplicacionesmoviles.data.Usuario
import com.example.prueba2aplicacionesmoviles.navegacion.Rutas
import kotlinx.coroutines.launch

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

        // Temporal: se reemplaza cuando llegue el login real de tu compañero.
        composable(Rutas.LOGIN) {
            MarcadorLogin(
                onIrADocumentos = { navController.navigate(Rutas.cargarDocumento(1)) }
            )
        }

        // Temporal: se reemplaza con el registro real.
        composable(Rutas.REGISTRO) { Marcador("Pantalla de registro") }

        composable(
            route = Rutas.CARGAR_DOCUMENTO,
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { entrada ->
            val usuarioId = entrada.arguments?.getInt("usuarioId") ?: return@composable
            PantallaCargarDocumento(
                usuarioId = usuarioId,
                onVolver = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun Marcador(texto: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(texto)
    }
}

// Temporal: crea un usuario de prueba y salta a cargar documentos.
@Composable
private fun MarcadorLogin(onIrADocumentos: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pantalla de inicio de sesión")
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val dao = AppDatabase.obtener(context).usuarioDao()
                if (dao.buscarPorId(1) == null) {
                    dao.insertar(
                        Usuario(
                            nombre = "Prueba",
                            correo = "prueba@test.cl",
                            passwordHash = "x",
                            salt = "x"
                        )
                    )
                }
                onIrADocumentos()
            }
        }) {
            Text("Ir a documentos (solo prueba)")
        }
    }
}