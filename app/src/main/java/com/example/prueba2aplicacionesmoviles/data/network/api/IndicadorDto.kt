package com.example.prueba2aplicacionesmoviles.data.network.api

import com.google.gson.annotations.SerializedName

data class MindicadorResponse(
    @SerializedName("version") val version: String? = null,
    @SerializedName("autor") val autor: String? = null,
    @SerializedName("uf") val uf: ElementoIndicadorDto? = null,
    @SerializedName("dolar") val dolar: ElementoIndicadorDto? = null,
    @SerializedName("euro") val euro: ElementoIndicadorDto? = null,
    @SerializedName("utm") val utm: ElementoIndicadorDto? = null
)

data class ElementoIndicadorDto(
    @SerializedName("codigo") val codigo: String? = null,
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("unidad_medida") val unidadMedida: String? = null,
    @SerializedName("fecha") val fecha: String? = null,
    @SerializedName("valor") val valor: Double? = null
)

data class IndicadorModel(
    val codigo: String,
    val nombre: String,
    val valorFormateado: String,
    val unidadMedida: String
)
