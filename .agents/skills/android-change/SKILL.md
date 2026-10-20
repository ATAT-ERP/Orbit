---
name: android-change
description: Corregir, extender o ajustar una funcionalidad existente de Orbit conservando sus contratos y comportamientos no afectados.
---

# Cambio en una funcionalidad existente

Usá también [`android-code`](../android-code/SKILL.md) cuando cambies Kotlin y las reglas de [`AGENTS.md`](../../../AGENTS.md).

1. Reproducí o describí el comportamiento actual con el código, las pruebas y la spec pertinente en [`specs/`](../../../specs/README.md). Identificá el problema, sus consumidores y el alcance exacto antes de editar.
2. Cambiá el punto responsable más cercano al problema. Reutilizá métodos y componentes existentes si su contrato sigue claro para los otros consumidores; evitá refactorizaciones preventivas.
3. Si afecta red o sesión, contrastá el contrato actual con [`docs/nexusback/INTEGRATION_RULES.md`](../../../docs/nexusback/INTEGRATION_RULES.md) y la documentación del módulo. Conservá los campos, errores y comportamiento anterior que la tarea no cambie.
4. Verificá el caso corregido y los flujos existentes que puedan verse afectados. Elegí pruebas y tareas Gradle proporcionales al cambio según [`docs/DEVELOPMENT.md`](../../../docs/DEVELOPMENT.md); actualizá la spec o el contrato documentado si cambió una decisión o integración verificada.
