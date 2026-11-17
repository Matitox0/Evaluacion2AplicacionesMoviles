package com.example.prueba2aplicacionesmoviles.data.network.api

import retrofit2.http.GET

interface MindicadorApiService {
    @GET("api")
    suspend fun obtenerIndicadores(): MindicadorResponse
}
