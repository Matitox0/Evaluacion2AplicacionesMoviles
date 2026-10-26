package com.example.prueba2aplicacionesmoviles.navegacion

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prueba2aplicacionesmoviles.di.Dependencias
import com.example.prueba2aplicacionesmoviles.ui.bienvenida.PantallaBienvenida
import com.example.prueba2aplicacionesmoviles.ui.home.HomeViewModel
import com.example.prueba2aplicacionesmoviles.ui.home.PantallaHome
import com.example.prueba2aplicacionesmoviles.ui.login.LoginViewModel
import com.example.prueba2aplicacionesmoviles.ui.login.PantallaLogin
import com.example.prueba2aplicacionesmoviles.ui.registro.PantallaRegistro
import com.example.prueba2aplicacionesmoviles.ui.registro.RegistroViewModel
import com.example.prueba2aplicacionesmoviles.ui.cargar.CargarDocumentoViewModel
import com.example.prueba2aplicacionesmoviles.ui.cargar.PantallaCargarDocumento

private const val DURACION_TRANSICION = 350

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val sesion = remember { Dependencias.sesion(context) }

    // Si ya hay sesión guardada, la app abre directo en el menú.
    val inicio = remember { if (sesion.haySesionActiva()) Rutas.HOME else Rutas.BIENVENIDA }

    NavHost(
        navController = navController,
        startDestination = inicio,
        // Transiciones animadas entre pantallas: avanzar desliza a la izquierda, volver a la derecha.
        enterTransition = {
            slideIntoContainer(SlideDirection.Start, tween(DURACION_TRANSICION)) + fadeIn(tween(DURACION_TRANSICION))
        },
        exitTransition = {
            slideOutOfContainer(SlideDirection.Start, tween(DURACION_TRANSICION)) + fadeOut(tween(DURACION_TRANSICION))
        },
        popEnterTransition = {
            slideIntoContainer(SlideDirection.End, tween(DURACION_TRANSICION)) + fadeIn(tween(DURACION_TRANSICION))
        },
        popExitTransition = {
            slideOutOfContainer(SlideDirection.End, tween(DURACION_TRANSICION)) + fadeOut(tween(DURACION_TRANSICION))
        }
    ) {
        composable(Rutas.BIENVENIDA) {
            PantallaBienvenida(
                onIrALogin = { navController.navigate(Rutas.LOGIN) },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }

        composable(Rutas.LOGIN) {
            val vm: LoginViewModel = viewModel {
                LoginViewModel(Dependencias.usuarioRepositorio(context), sesion)
            }
            PantallaLogin(
                viewModel = vm,
                onVolver = { navController.popBackStack() },
                onIrARegistro = {
                    navController.navigate(Rutas.REGISTRO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onLoginExitoso = { navController.irAlMenu() }
            )
        }

        composable(Rutas.REGISTRO) {
            val vm: RegistroViewModel = viewModel {
                RegistroViewModel(Dependencias.usuarioRepositorio(context), sesion)
            }
            PantallaRegistro(
                viewModel = vm,
                onVolver = { navController.popBackStack() },
                onIrALogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                onRegistroExitoso = { navController.irAlMenu() }
            )
        }

        composable(Rutas.HOME) {
            val vm: HomeViewModel = viewModel {
                HomeViewModel(Dependencias.usuarioRepositorio(context), sesion)
            }
            PantallaHome(
                viewModel = vm,
                onSesionInvalida = { navController.limpiarYNavegar(Rutas.BIENVENIDA) },
                onCargarDocumento = { navController.navigate(Rutas.CARGAR_DOCUMENTO) }
            )
        }
        composable(Rutas.CARGAR_DOCUMENTO) {
            val vm: CargarDocumentoViewModel = viewModel {
                CargarDocumentoViewModel(Dependencias.documentoRepositorio(context), sesion)
            }
            PantallaCargarDocumento(
                viewModel = vm,
                onVolver = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}

// Entra al menú borrando el historial: el botón "atrás" no vuelve al login.
private fun NavHostController.irAlMenu() = limpiarYNavegar(Rutas.HOME)

private fun NavHostController.limpiarYNavegar(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
