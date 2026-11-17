package com.example.prueba2aplicacionesmoviles.data.repositorio

import android.util.Log
import com.example.prueba2aplicacionesmoviles.data.network.api.ClienteApiExterna
import com.example.prueba2aplicacionesmoviles.data.network.api.IndicadorModel
import com.example.prueba2aplicacionesmoviles.data.network.api.MindicadorApiService
import java.text.NumberFormat
import java.util.Locale

sealed interface EstadoIndicadores {
    data object Cargando : EstadoIndicadores
    data class Exito(val lista: List<IndicadorModel>, val esFallback: Boolean = false) : EstadoIndicadores
    data class Error(val mensaje: String) : EstadoIndicadores
}

class IndicadorRepositorio(
    private val apiService: MindicadorApiService = ClienteApiExterna.servicio
) {
    suspend fun obtenerIndicadores(): EstadoIndicadores {
        return try {
            val response = apiService.obtenerIndicadores()
            val lista = mutableListOf<IndicadorModel>()
            val formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))

            response.uf?.let {
                val valorStr = it.valor?.let { v -> formatoMoneda.format(v) } ?: "$0"
                lista.add(IndicadorModel("UF", it.nombre ?: "Unidad de Fomento", valorStr, it.unidadMedida ?: "Pesos"))
            }
            response.dolar?.let {
                val valorStr = it.valor?.let { v -> formatoMoneda.format(v) } ?: "$0"
                lista.add(IndicadorModel("USD", it.nombre ?: "Dólar Observado", valorStr, it.unidadMedida ?: "Pesos"))
            }
            response.euro?.let {
                val valorStr = it.valor?.let { v -> formatoMoneda.format(v) } ?: "$0"
                lista.add(IndicadorModel("EUR", it.nombre ?: "Euro", valorStr, it.unidadMedida ?: "Pesos"))
            }
            response.utm?.let {
                val valorStr = it.valor?.let { v -> formatoMoneda.format(v) } ?: "$0"
                lista.add(IndicadorModel("UTM", it.nombre ?: "UTM", valorStr, it.unidadMedida ?: "Pesos"))
            }

            if (lista.isNotEmpty()) {
                EstadoIndicadores.Exito(lista)
            } else {
                EstadoIndicadores.Exito(obtenerIndicadoresFallback(), esFallback = true)
            }
        } catch (e: Exception) {
            Log.e("IndicadorRepositorio", "Error de red al consultar API externa: ${e.message}", e)
            EstadoIndicadores.Exito(obtenerIndicadoresFallback(), esFallback = true)
        }
    }

    private fun obtenerIndicadoresFallback(): List<IndicadorModel> {
        val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
        return listOf(
            IndicadorModel("UF", "Unidad de Fomento", formato.format(41350.42), "Pesos"),
            IndicadorModel("USD", "Dólar Observado", formato.format(1001.17), "Pesos"),
            IndicadorModel("EUR", "Euro", formato.format(1102.0), "Pesos"),
            IndicadorModel("UTM", "Unidad Tributaria Mensual", formato.format(72440.0), "Pesos")
        )
    }
}
