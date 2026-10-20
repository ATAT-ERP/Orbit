# Orbit

Cliente Android nativo de A.T.A.T. La base usa Kotlin, Jetpack Compose y Material 3.

## Requisitos de desarrollo

- Android Studio Koala (2024.1.1), JDK 17 y Android SDK Platform 34.
- Un emulador o dispositivo Android con API 26 o superior para ejecutar la app.
- Abrir la raíz de este repositorio en Android Studio. El único módulo es `app`.

Android Studio puede crear `local.properties` con la ruta del SDK de cada equipo. Este archivo y `.idea/` son locales y no se versionan.

## Versiones fijadas

| Componente | Versión |
| --- | --- |
| Android Gradle Plugin | 8.5.2 |
| Gradle Wrapper | 8.7 |
| Kotlin | 1.9.0 |
| Compose Compiler | 1.5.1 |
| Compose BOM | 2024.04.01 |
| Material 3 | 1.2.1 (resuelta por Compose BOM) |
| Activity Compose | 1.9.0 |
| compileSdk / targetSdk / minSdk | 34 / 34 / 26 |

Las dependencias AndroidX y de pruebas están fijadas en `gradle/libs.versions.toml`.

## Compilación

Sincronizar el proyecto con Gradle en Android Studio y ejecutar en la raíz:

```powershell
.\gradlew.bat :app:assembleDebug
```

Después, seleccionar un dispositivo y ejecutar la configuración `app` de Android Studio. La pantalla inicial se implementa íntegramente con Compose.
