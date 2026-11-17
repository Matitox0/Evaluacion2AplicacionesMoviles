package com.example.prueba2aplicacionesmoviles

import com.example.prueba2aplicacionesmoviles.data.network.api.ElementoIndicadorDto
import com.example.prueba2aplicacionesmoviles.data.network.api.MindicadorApiService
import com.example.prueba2aplicacionesmoviles.data.network.api.MindicadorResponse
import com.example.prueba2aplicacionesmoviles.data.repositorio.EstadoIndicadores
import com.example.prueba2aplicacionesmoviles.data.repositorio.IndicadorRepositorio
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MockMindicadorApiService(
    private val respuesta: MindicadorResponse? = null,
    private val lanzarExcepcion: Boolean = false
) : MindicadorApiService {
    override suspend fun obtenerIndicadores(): MindicadorResponse {
        if (lanzarExcepcion) {
            throw RuntimeException("Error simulado de red")
        }
        return respuesta ?: MindicadorResponse()
    }
}

class IndicadorRepositorioTest {

    @Test
    fun obtenerIndicadores_exito() = runBlocking {
        val mockResponse = MindicadorResponse(
            uf = ElementoIndicadorDto("uf", "Unidad de fomento", "Pesos", "2026-03-31", 38900.0),
            dolar = ElementoIndicadorDto("dolar", "Dólar observado", "Pesos", "2026-03-31", 950.0)
        )
        val apiService = MockMindicadorApiService(respuesta = mockResponse)
        val repositorio = IndicadorRepositorio(apiService)

        val resultado = repositorio.obtenerIndicadores()

        assertTrue(resultado is EstadoIndicadores.Exito)
        val exito = resultado as EstadoIndicadores.Exito
        assertFalse(exito.esFallback)
        assertEquals(2, exito.lista.size)
        assertEquals("UF", exito.lista[0].codigo)
        assertEquals("USD", exito.lista[1].codigo)
    }

    @Test
    fun obtenerIndicadores_errorConexion_usaFallback() = runBlocking {
        val apiService = MockMindicadorApiService(lanzarExcepcion = true)
        val repositorio = IndicadorRepositorio(apiService)

        val resultado = repositorio.obtenerIndicadores()

        assertTrue(resultado is EstadoIndicadores.Exito)
        val exito = resultado as EstadoIndicadores.Exito
        assertTrue(exito.esFallback)
        assertTrue(exito.lista.isNotEmpty())
    }
}
