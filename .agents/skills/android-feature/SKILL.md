---
name: android-feature
description: Desarrollar una funcionalidad nueva de Orbit a partir de su spec, incorporando solo los componentes Android y contratos que el flujo requiera.
---

# Funcionalidad nueva de Orbit

Usá también [`android-code`](../android-code/SKILL.md) para la implementación Kotlin y las reglas de [`AGENTS.md`](../../../AGENTS.md).

1. Leé la spec correspondiente en [`specs/`](../../../specs/README.md) y separá las decisiones de Orbit de los contratos confirmados y pendientes. Identificá los estados y escenarios observables que exige.
2. Inspeccioná componentes y flujos existentes. Incorporá Screen, ViewModel, Repository y DTO solo donde tengan trabajo real, siguiendo [`docs/ARCHITECTURE.md`](../../../docs/ARCHITECTURE.md). Mantené el flujo de datos desde NexusBack hasta el estado de pantalla y las acciones de vuelta al ViewModel.
3. Si la funcionalidad consume NexusBack, confirmá rutas, campos, autenticación y errores según [`docs/nexusback/INTEGRATION_RULES.md`](../../../docs/nexusback/INTEGRATION_RULES.md). Registrá el contrato móvil verificado en esa carpeta junto con la integración; no implementes detalles marcados como pendientes por suposición.
4. Representá carga, datos o vacío y errores cuando sean relevantes para el flujo; ofrecé reintento o corrección donde la spec lo pida. Conservá los códigos funcionales `NEX-*` recibidos sin confundirlos con errores de transporte.
5. Agregá pruebas de comportamiento cuando aporten evidencia útil y ejecutá las verificaciones relacionadas de [`docs/DEVELOPMENT.md`](../../../docs/DEVELOPMENT.md). No agregues pruebas que solo reflejen la estructura del código.
