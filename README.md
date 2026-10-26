# Aplicación de Gestión de Documentos (Evaluación Parcial 2 – DSY1105)

Aplicación Android desarrollada con Kotlin y Jetpack Compose, arquitectura MVVM, base de datos local Room y acceso a recursos nativos (selector de archivos y cámara).

## Integrantes
- Matias Meza
- Alonso Muñoz

## Contexto / problemática
Estudiantes y trabajadores necesitan tener a mano documentos importantes (certificados, boletas, cédula, contratos) sin depender de internet. La app permite que cada usuario, con su propia cuenta, digitalice documentos con la cámara o los importe desde el teléfono y los guarde de forma privada en el dispositivo.

## Funcionalidades implementadas
1. **Registro e inicio de sesión** con formularios validados campo a campo: ícono por campo, mensaje de error con ícono rojo, check verde cuando el campo es correcto, indicador animado de seguridad de contraseña y aceptación de términos.
2. **Contraseñas seguras:** se guardan como hash PBKDF2 con sal aleatoria, nunca en texto plano.
3. **Sesión persistente** con `SharedPreferences`: al reabrir la app se entra directo al menú. Cierre de sesión con diálogo de confirmación.
4. **Cargar documento:** formulario validado (archivo obligatorio + nombre de 3 a 60 caracteres). El archivo puede venir del selector del sistema o de la cámara.
5. **Mis documentos:** lista reactiva (Room + `Flow`) que se actualiza sola; tocar un documento lo abre con la app correspondiente (visor PDF, galería) y eliminar pide confirmación.

## Recursos nativos
| Recurso | Implementación | Seguridad |
|---|---|---|
| Almacenamiento / archivos | `ActivityResultContracts.OpenDocument` (Storage Access Framework). El archivo se copia a la carpeta privada de la app. | No requiere permisos de almacenamiento: el usuario elige qué archivo compartir. |
| Cámara | `ActivityResultContracts.TakePicture` + `FileProvider`. La foto se escribe en un archivo temporal y pasa al mismo formulario de carga. | No requiere el permiso `CAMERA` (usa la app de cámara del sistema). Se verifica que el dispositivo tenga cámara y los temporales se borran al salir. |
| Abrir documentos | `Intent.ACTION_VIEW` con `FileProvider` y `FLAG_GRANT_READ_URI_PERMISSION`. | Nunca se expone la ruta real del archivo; el permiso de lectura es temporal y solo para ese archivo. |

## Animaciones
- Transiciones deslizantes entre pantallas (Navigation Compose).
- Entrada escalonada de las tarjetas del menú y de la pantalla de bienvenida.
- Sacudida del formulario cuando se intenta enviar con errores.
- Banner de error y mensajes que aparecen/desaparecen animados.
- Barra de seguridad de contraseña con color y progreso animados.
- Botón que cambia a indicador de carga mientras se procesa.
- Reordenamiento animado de la lista de documentos al agregar o eliminar.

## Arquitectura (MVVM)
```
app/src/main/java/com/example/prueba2aplicacionesmoviles/
├── data/                 Entidades y DAOs de Room (Usuario, Documento) y AppDatabase
│   ├── repositorio/      UsuarioRepositorio, DocumentoRepositorio (única puerta a los datos)
│   ├── seguridad/        HashPassword (PBKDF2 + sal)
│   └── sesion/           SesionManager (SharedPreferences)
├── di/                   Dependencias: arma repositorios para los ViewModels
├── dominio/validacion/   Validador y ValidadorDocumento: reglas en Kotlin puro, sin Android
├── navegacion/           Rutas y AppNavegacion (NavHost + transiciones)
└── ui/                   Una carpeta por pantalla (Pantalla + ViewModel) y componentes reutilizables
```
- Las **pantallas** solo dibujan el estado (`StateFlow`) y envían eventos al ViewModel.
- Los **ViewModels** aplican las reglas de `dominio/validacion` y llaman a los repositorios.
- Las **validaciones** no dependen de Compose ni de Android, por eso tienen pruebas unitarias en `app/src/test`.

## Pasos para ejecutar
1. Clonar el repositorio y abrirlo en **Android Studio**.
2. Esperar la sincronización de Gradle.
3. Ejecutar la configuración `app` en un emulador o dispositivo con **Android 7.0 (API 24) o superior**.
4. Para probar la cámara en el emulador, usar un AVD con cámara habilitada (Virtual scene o Webcam).

### Pruebas unitarias
```bash
./gradlew test
```

## Planificación
- Repositorio GitHub: _agregar enlace_
- Tablero Trello: _agregar enlace_
