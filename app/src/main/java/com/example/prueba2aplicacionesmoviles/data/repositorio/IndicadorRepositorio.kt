package com.example.prueba2aplicacionesmoviles.data.repositorio

import com.example.prueba2aplicacionesmoviles.data.network.api.ClienteApiExterna
import com.example.prueba2aplicacionesmoviles.data.network.api.IndicadorModel
import com.example.prueba2aplicacionesmoviles.data.network.api.MindicadorApiService
import java.text.NumberFormat
import java.util.Locale

sealed interface EstadoIndicadores {
    data object Cargando : EstadoIndicadores
    data class Exito(val lista: List<IndicadorModel>) : EstadoIndicadores
    data class Error(val mensaje: String) : EstadoIndicadores
}

class IndicadorRepositorio(
    private val apiService: MindicadorApiService = ClienteApiExterna.servicio
) {
    suspend fun obtenerIndicadores(): EstadoIndicadores {
        return try {
            val response = apiService.obtenerIndicadores()
            val lista = mutableListOf<IndicadorModel>()
            val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

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
                EstadoIndicadores.Error("No se pudieron cargar los indicadores económicos.")
            }
        } catch (e: Exception) {
            EstadoIndicadores.Error("Error de conexión al obtener indicadores: ${e.localizedMessage ?: "desconocido"}")
        }
    }
}
