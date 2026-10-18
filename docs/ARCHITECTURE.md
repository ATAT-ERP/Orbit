# Arquitectura de Orbit

## Alcance y estado actual

Orbit es el cliente Android liviano del ERP A.T.A.T. Presenta datos, recoge acciones del usuario, gestiona el estado de cada pantalla y se comunica con los servicios existentes. NexusBack conserva la lógica de negocio, las autorizaciones, las reglas de integridad y la persistencia. Supabase Auth es el proveedor de autenticación; Orbit utilizará la sesión y el JWT para llamar a los endpoints protegidos de NexusBack. El backend debe verificar el token y decidir los permisos: la interfaz no es una barrera de seguridad.

La arquitectura acordada para las funcionalidades nuevas es **MVVM + Repository**. El repositorio contiene por ahora solo una pantalla inicial en Compose; las capas de datos, los ViewModels, StateFlow, la autenticación y la inyección de dependencias descritos aquí son criterios de implementación, no componentes ya presentes. No se introduce inicialmente Clean Architecture estricta, una capa de casos de uso independientes ni modelos de dominio duplicados.

## Flujo y responsabilidades

```text
Screen (Compose) → ViewModel → Repository → API de NexusBack
       ↑                ↑           ↓              ↓
       └── estado UI ───┘       DTO/error HTTP ← respuesta
```

- **`ui`**: pantallas, componentes Compose, navegación y ViewModels. La pantalla renderiza el estado y envía acciones al ViewModel. El ViewModel coordina la carga, el envío y la presentación de resultados, sin conocer detalles HTTP ni implementar reglas de negocio.
- **`data/model`**: DTO que representan las estructuras JSON verificadas de NexusBack. Usar los nombres y tipos del contrato. Crear modelos de UI o conversiones solo cuando la pantalla tenga una necesidad concreta que el DTO no cubra.
- **`data/network`**: cliente HTTP, configuración de URL base, encabezado `Authorization: Bearer <JWT>`, serialización y manejo técnico de respuestas. Mantener aquí los detalles del transporte; no decidir permisos ni reinterpretar errores funcionales.
- **`data/repository`**: acceso a las operaciones que necesita la app. Coordina el cliente de red, expone resultados y errores comprensibles para el ViewModel y concentra cualquier política compartida de datos que realmente se requiera. Un repositorio puede atender varias pantallas del mismo recurso o módulo.

Las carpetas indican responsabilidades previstas, no una obligación de crear archivos vacíos. Una dependencia debe apuntar hacia los datos: `ui` usa repositorios; los repositorios usan red y modelos. Evitar que una pantalla llame directamente al cliente HTTP.

## Estado y límites de la lógica

Cada ViewModel expondrá un `StateFlow` de estado de pantalla de solo lectura para la UI y recibirá acciones mediante métodos claros. El estado incluirá solo lo necesario para renderizar, por ejemplo datos, carga y error. Se actualiza desde el ViewModel; Compose lo observa con una API compatible con el ciclo de vida cuando se incorpore la dependencia correspondiente. Evitar varios booleanos que permitan estados contradictorios y no guardar el mismo dato en varias fuentes de estado sin motivo.

Orbit puede validar entradas para dar respuesta inmediata, controlar navegación, formato visual y disponibilidad de controles. NexusBack vuelve a validar cada operación, aplica reglas de negocio y permisos, y determina el resultado. No replicar cálculos, decisiones de autorización ni reglas de integridad del ERP en ViewModels o repositorios. Conservar los códigos funcionales `NEX-*` para que la UI pueda mostrar un mensaje apropiado sin perder el código original.

## Dependencias y reutilización

Cuando se incorporen clientes y repositorios, ensamblarlos mediante un contenedor manual pequeño, creado en un punto de entrada de la aplicación y compartido con los ViewModels que lo necesiten. El contenedor proveerá instancias y configuración; no debe contener operaciones de negocio. Pasar dependencias explícitamente para que puedan sustituirse en pruebas. Añadir un framework de inyección solo si una necesidad real justifica su costo.

Reutilizar un componente Compose cuando se repita una interacción o presentación reconocible; mantener los detalles exclusivos de una pantalla junto a ella. Reutilizar un repositorio por recurso o responsabilidad compartida, no crear uno por cada pantalla ni uno global que concentre toda la app. Usar DTO directos mientras el contrato sirva a la UI; introducir un modelo adicional o mapper solo ante una diferencia real entre el contrato y una necesidad estable de presentación. Preferir herramientas oficiales de Kotlin y Android y clases pequeñas con una responsabilidad clara; evitar interfaces, helpers y abstracciones preventivas.

Las reglas y contratos de integración se mantienen en [nexusback/README.md](nexusback/README.md) y [nexusback/INTEGRATION_RULES.md](nexusback/INTEGRATION_RULES.md).
