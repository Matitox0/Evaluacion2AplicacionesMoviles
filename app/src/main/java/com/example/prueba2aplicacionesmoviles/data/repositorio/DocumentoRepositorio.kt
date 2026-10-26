package com.example.prueba2aplicacionesmoviles.data.repositorio

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.prueba2aplicacionesmoviles.data.Documento
import com.example.prueba2aplicacionesmoviles.data.DocumentoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class DocumentoRepository(
    private val context: Context,
    private val documentoDao: DocumentoDao
) {

    // La usaremos en el paso de "ver documentos".
    fun listar(usuarioId: Int): Flow<List<Documento>> =
        documentoDao.listarPorUsuario(usuarioId)

    // Nombre original del archivo elegido en el selector.
    suspend fun nombreOriginal(uri: Uri): String = withContext(Dispatchers.IO) {
        context.contentResolver
            .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
            ?: "documento"
    }

    // Copia el archivo a la carpeta privada del usuario y registra el documento.
    suspend fun guardar(usuarioId: Int, nombre: String, origen: Uri): Boolean =
        withContext(Dispatchers.IO) {
            var archivoCreado: File? = null
            try {
                val resolver = context.contentResolver
                val tipo = resolver.getType(origen) ?: "application/octet-stream"

                // Solo letras y números en la extensión.
                val extension = nombreOriginal(origen)
                    .substringAfterLast('.', "")
                    .filter { it.isLetterOrDigit() }
                    .take(10)

                val carpeta = File(context.filesDir, "documentos/$usuarioId").apply { mkdirs() }
                val nombreArchivo = System.currentTimeMillis().toString() +
                        if (extension.isNotEmpty()) ".$extension" else ""
                val archivo = File(carpeta, nombreArchivo)
                archivoCreado = archivo

                resolver.openInputStream(origen)?.use { entrada ->
                    archivo.outputStream().use { salida -> entrada.copyTo(salida) }
                } ?: return@withContext false

                documentoDao.insertar(
                    Documento(
                        usuarioId = usuarioId,
                        nombre = nombre.trim(),
                        uri = Uri.fromFile(archivo).toString(),
                        tipoMime = tipo,
                        fechaCarga = System.currentTimeMillis()
                    )
                )
                true
            } catch (e: CancellationException) {
                archivoCreado?.delete()
                throw e
            } catch (e: Exception) {
                // Si algo falló a medias, no dejamos un archivo huérfano.
                archivoCreado?.delete()
                false
            }
        }

    suspend fun eliminar(documento: Documento) = withContext(Dispatchers.IO) {
        try {
            val fileUri = Uri.parse(documento.uri)
            fileUri.path?.let { File(it).delete() }
        } catch (_: Exception) {}
        documentoDao.eliminar(documento)
    }
}