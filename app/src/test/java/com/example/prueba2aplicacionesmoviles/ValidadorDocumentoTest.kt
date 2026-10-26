package com.example.prueba2aplicacionesmoviles

import com.example.prueba2aplicacionesmoviles.dominio.validacion.ValidadorDocumento
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas de las reglas del formulario "Cargar documento".
class ValidadorDocumentoTest {

    @Test
    fun nombreValido() {
        assertTrue(ValidadorDocumento.validarNombre("Certificado alumno regular").esValido)
        assertTrue(ValidadorDocumento.validarNombre("Foto 26-10-2026 14:30").esValido)
    }

    @Test
    fun nombreVacioOCortoEsInvalido() {
        assertFalse(ValidadorDocumento.validarNombre("").esValido)
        assertFalse(ValidadorDocumento.validarNombre("   ").esValido)
        assertFalse(ValidadorDocumento.validarNombre("ab").esValido)
    }

    @Test
    fun nombreDemasiadoLargoEsInvalido() {
        val largo = "a".repeat(ValidadorDocumento.NOMBRE_MAX + 1)
        assertFalse(ValidadorDocumento.validarNombre(largo).esValido)
    }

    @Test
    fun archivoObligatorio() {
        assertFalse(ValidadorDocumento.validarArchivo(false).esValido)
        assertTrue(ValidadorDocumento.validarArchivo(true).esValido)
    }
}
