# Aplicación de Gestión de Documentos (Evaluación Parcial 2)

Aplicación desarrollada en Android Studio con Jetpack Compose, arquitectura MVVM, base de datos local Room y recursos nativos.

## Integrantes del Grupo
- Matias Meza
- Alonso Muñoz

## Descripción del Proyecto
Aplicación móvil orientada a la autenticación segura de usuarios, gestión de sesiones persistentes, carga de documentos desde el almacenamiento y captura de fotos mediante la cámara del dispositivo.

## Funcionalidades y Recursos Nativos Implementados
1. **Autenticación y Registro:** Formularios validados en tiempo real con la lógica desacoplada y retroalimentación visual ante errores.
2. **Sesión Persistente y Cierre de Sesión:** Manejo de sesiones con `SharedPreferences` y botón de cierre de sesión en el menú principal y barra superior.
3. **Recurso Nativo 1 (Almacenamiento/Archivos):** Selección y copiado seguro de documentos (PDF, imágenes, textos) al almacenamiento privado de la app.
4. **Recurso Nativo 2 (Cámara):** Captura de fotografías mediante `TakePicture` y `FileProvider` para digitalizar documentos directo a la base de datos Room.
5. **Persistencia Local:** Use of Room Database para almacenar los documentos asociados al usuario activo.

## Pasos para Ejecutar
1. Clonar o abrir el proyecto en **Android Studio**.
2. Sincronizar el proyecto con Gradle.
3. Ejecutar la aplicación en un emulador Android (API 26+) o dispositivo físico con depuración USB habilitada.
