package com.example.prueba2aplicacionesmoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.prueba2aplicacionesmoviles.navegacion.AppNavegacion
import com.example.prueba2aplicacionesmoviles.ui.theme.Prueba2AplicacionesMovilesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prueba2AplicacionesMovilesTheme {
                AppNavegacion()
            }
        }
    }
}
