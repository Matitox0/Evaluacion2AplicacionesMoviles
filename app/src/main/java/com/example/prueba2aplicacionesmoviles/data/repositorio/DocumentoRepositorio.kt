package com.example.prueba2aplicacionesmoviles.data.repositorio

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.prueba2aplicacionesmoviles.data.Documento
import com.example.prueba2aplicacionesmoviles.data.DocumentoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

// Única puerta de entrada a los documentos: maneja tanto la tabla de Room como los archivos en disco.
class DocumentoRepositorio(
    private val context: Context,
    private val documentoDao: DocumentoDao
) {

    private val autoridad = "${context.packageName}.fileprovider"
    private val carpetaCamara get() = File(context.cacheDir, CARPETA_CAMARA)

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
        archivoDe(documento)?.delete()
        documentoDao.eliminar(documento)
    }

    /**
     * Uri "content://" para que otra app (visor PDF, galería) pueda abrir el documento.
     * Nunca se expone la ruta real del archivo: FileProvider da acceso temporal solo a ese archivo.
     * Devuelve null si el archivo ya no existe en disco.
     */
    fun uriParaAbrir(documento: Documento): Uri? {
        val archivo = archivoDe(documento)?.takeIf { it.exists() } ?: return null
        return FileProvider.getUriForFile(context, autoridad, archivo)
    }

    // Archivo temporal donde la app de cámara escribe la foto antes de que el usuario la guarde.
    fun crearUriFoto(): Uri {
        val archivo = File(carpetaCamara.apply { mkdirs() }, "foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, autoridad, archivo)
    }

    // Las fotos ya guardadas fueron copiadas a la carpeta privada; las temporales se pueden borrar.
    fun limpiarFotosTemporales() {
        carpetaCamara.listFiles()?.forEach { it.delete() }
    }

    private fun archivoDe(documento: Documento): File? =
        Uri.parse(documento.uri).path?.let(::File)

    private companion object {
        const val CARPETA_CAMARA = "camara"
    }
}
