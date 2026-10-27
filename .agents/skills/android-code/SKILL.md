---
name: android-code
description: Implementar o revisar código Kotlin y Jetpack Compose de Orbit, tanto nuevo como existente; usar junto con android-feature o android-change cuando corresponda.
---

# Código Android de Orbit

Aplicá las reglas generales de [`AGENTS.md`](../../../AGENTS.md). Consultá [`docs/ARCHITECTURE.md`](../../../docs/ARCHITECTURE.md) para ubicar responsabilidades y [`docs/DEVELOPMENT.md`](../../../docs/DEVELOPMENT.md) para convenciones y verificaciones.

- Escribí cada función alrededor de una responsabilidad reconocible. Si crece, separá solo una responsabilidad real; no extraigas helpers de un solo uso para esconder un flujo simple.
- Reutilizá un comportamiento cuando resuelva el mismo problema. Antes de ampliar una función compartida, revisá sus consumidores y evitá convertirla en una colección de flags o ramas para casos distintos.
- Dejá la presentación y las acciones de usuario en Compose, el estado y la coordinación de pantalla en el ViewModel, y el acceso a datos en Repository. No dupliques estado ni validaciones entre capas sin una necesidad concreta.
- Usá los DTO directamente cuando representen bien el contrato y la pantalla. Introducí una conversión solo si existe una diferencia estable que la UI necesite.
- Al revisar el resultado, buscá ramas, capturas de errores, variables, comentarios y abstracciones que no aporten al caso real. Comprobá el comportamiento afectado con las verificaciones proporcionales al cambio.
