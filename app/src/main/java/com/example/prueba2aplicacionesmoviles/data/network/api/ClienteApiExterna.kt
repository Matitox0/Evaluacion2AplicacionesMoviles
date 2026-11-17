package com.example.prueba2aplicacionesmoviles.data.network.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteApiExterna {
    private const val BASE_URL = "https://mindicador.cl/"

    val servicio: MindicadorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApiService::class.java)
    }
}
