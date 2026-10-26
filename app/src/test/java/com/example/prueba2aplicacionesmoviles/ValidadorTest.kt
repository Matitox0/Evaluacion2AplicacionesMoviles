package com.example.prueba2aplicacionesmoviles

import com.example.prueba2aplicacionesmoviles.dominio.validacion.FuerzaPassword
import com.example.prueba2aplicacionesmoviles.dominio.validacion.Validador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas de la lógica de validación, sin emulador: demuestra que no depende de la UI.
class ValidadorTest {

    @Test
    fun nombreValidoConTildes() {
        assertTrue(Validador.validarNombre("José Muñoz").esValido)
    }

    @Test
    fun nombreVacioOConNumerosEsInvalido() {
        assertFalse(Validador.validarNombre("   ").esValido)
        assertFalse(Validador.validarNombre("Ana2").esValido)
        assertFalse(Validador.validarNombre("Al").esValido)
    }

    @Test
    fun correoConFormatoCorrecto() {
        assertTrue(Validador.validarCorreo("alumno@duocuc.cl").esValido)
        assertTrue(Validador.validarCorreo("  alumno@duocuc.cl ").esValido)
    }

    @Test
    fun correoConFormatoIncorrecto() {
        assertFalse(Validador.validarCorreo("").esValido)
        assertFalse(Validador.validarCorreo("alumno@").esValido)
        assertFalse(Validador.validarCorreo("alumno.duocuc.cl").esValido)
    }

    @Test
    fun passwordRegistroExigeReglas() {
        assertFalse(Validador.validarPasswordRegistro("corta1A").esValido)
        assertFalse(Validador.validarPasswordRegistro("sinmayuscula1").esValido)
        assertFalse(Validador.validarPasswordRegistro("SINMINUSCULA1").esValido)
        assertFalse(Validador.validarPasswordRegistro("SinNumeros").esValido)
        assertFalse(Validador.validarPasswordRegistro("Con Espacio1").esValido)
        assertTrue(Validador.validarPasswordRegistro("Segura123").esValido)
    }

    @Test
    fun confirmacionDebeCoincidir() {
        assertFalse(Validador.validarConfirmacion("Segura123", "Segura124").esValido)
        assertTrue(Validador.validarConfirmacion("Segura123", "Segura123").esValido)
    }

    @Test
    fun terminosObligatorios() {
        assertFalse(Validador.validarTerminos(false).esValido)
        assertTrue(Validador.validarTerminos(true).esValido)
    }

    @Test
    fun fuerzaPassword() {
        assertEquals(FuerzaPassword.VACIA, Validador.fuerzaPassword(""))
        assertEquals(FuerzaPassword.DEBIL, Validador.fuerzaPassword("abc"))
        assertEquals(FuerzaPassword.MEDIA, Validador.fuerzaPassword("Segura123"))
        assertEquals(FuerzaPassword.FUERTE, Validador.fuerzaPassword("Segura123!"))
    }
}
