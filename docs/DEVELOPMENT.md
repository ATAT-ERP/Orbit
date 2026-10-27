# Desarrollo de Orbit

## Requisitos y versiones

Abrir la raíz del repositorio con Android Studio Koala (2024.1.1), JDK 17 y Android SDK Platform 34. Para ejecutar la app se necesita un emulador o dispositivo Android con API 26 o superior. El único módulo Gradle es `app`.

Las versiones fijadas por el proyecto son: Android Gradle Plugin 8.5.2, Gradle Wrapper 8.7, Kotlin 1.9.0, Compose Compiler 1.5.1, Compose BOM 2024.04.01 y Activity Compose 1.9.0. El catálogo completo está en `gradle/libs.versions.toml`; `compileSdk` y `targetSdk` son 34 y `minSdk` es 26, según `app/build.gradle.kts`. El BOM resuelve la versión de Material 3; no fijarla por separado en las instrucciones de desarrollo.

## Abrir, compilar y ejecutar

1. Abrir la carpeta raíz en Android Studio y configurar JDK 17 como Gradle JDK.
2. Instalar SDK Platform 34 y las herramientas Android que solicite el IDE. Dejar que Android Studio genere `local.properties` con la ruta local del SDK si hace falta.
3. Sincronizar el proyecto con **Sync Project with Gradle Files**.
4. Seleccionar un emulador o dispositivo y ejecutar la configuración `app`.

En Windows, desde la raíz:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
```

La prueba instrumentada requiere un dispositivo conectado o emulador iniciado:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

En macOS o Linux usar `./gradlew` con las mismas tareas. Ejecutar las verificaciones que correspondan al cambio antes de abrir el Pull Request. Las pruebas actuales son las plantillas iniciales; añadir pruebas que comprueben comportamiento real al implementar funcionalidades.

### Dispositivo físico

Activar las opciones de desarrollador y la depuración USB, conectar el teléfono por USB y aceptar en él la autorización de depuración. Confirmar que aparece en el selector de dispositivos de Android Studio o con `adb devices`; luego ejecutar `app`. Si no aparece, revisar cable, modo USB y controlador del dispositivo. La versión Android debe cumplir API 26 o superior. Para probar llamadas a NexusBack desde el teléfono, usar una URL alcanzable desde ese dispositivo; `localhost` en el teléfono apunta al propio teléfono.

## Entornos y secretos

La base actual no contiene configuración de endpoints, Supabase Auth ni variantes de entorno. Definir la URL de NexusBack y los datos públicos de configuración de Supabase al implementar la integración, con una fuente explícita por entorno y sin fijar direcciones locales en código compartido. Documentar entonces las claves necesarias y cómo se selecciona cada entorno. No subir tokens, contraseñas, claves de servicio o material de firma. `local.properties`, `.idea/` y archivos de firma están excluidos por `.gitignore`; comprobar el diff antes de cada commit. Una clave pública de cliente no sustituye la validación de permisos y tokens en NexusBack.

## Convenciones de implementación

- Seguir el estilo oficial de Kotlin (`kotlin.code.style=official`), usar nombres descriptivos y mantener funciones y clases con responsabilidad clara.
- Usar las APIs oficiales de Kotlin, coroutines, Android y Compose cuando cubran el problema. Incorporar dependencias al catálogo de versiones cuando sean necesarias.
- Organizar funcionalidades según las responsabilidades de [ARCHITECTURE.md](ARCHITECTURE.md): pantallas y ViewModels en `ui`, DTO en `data/model`, transporte en `data/network` y acceso a datos en `data/repository`.
- Mantener los ViewModels centrados en acciones y estado de pantalla. Evitar reglas de negocio locales, interfaces, helpers, mappers y capas adicionales sin un caso concreto.
- Usar DTO directos si sirven al contrato y a la pantalla. Centralizar código compartido solo cuando exista uso compartido; dividir clases que acumulen responsabilidades distintas.
- Al conectar un módulo, comprobar sus rutas, métodos, campos, autenticación y errores contra NexusBack antes de codificarlos. Seguir [las reglas de integración](nexusback/INTEGRATION_RULES.md).

## Ramas y revisión

Partir de `develop`, crear una rama corta para cada cambio y usar commits que describan lo hecho. Antes de subirla, revisar el diff, compilar y ejecutar las pruebas pertinentes. Abrir un Pull Request hacia la rama base acordada, explicar el alcance, las verificaciones realizadas y cualquier contrato de API afectado. Incorporar los comentarios de revisión y evitar mezclar cambios ajenos al objetivo del PR.
