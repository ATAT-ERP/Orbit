# Instrucciones para agentes de Orbit

## Fuentes y arquitectura

- Antes de implementar, consultá la documentación pertinente en [`docs/`](docs/ARCHITECTURE.md), la spec de la funcionalidad en [`specs/`](specs/README.md) y el código existente. Seguí sus convenciones reales; distinguí lo ya implementado de lo planificado.
- Orbit es un cliente Android con Kotlin y Jetpack Compose. Para funcionalidades nuevas, seguí MVVM + Repository y el flujo `Screen → ViewModel → Repository → NexusBack` descrito en [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md). La base actual solo tiene una pantalla inicial; no crees capas o archivos vacíos para completar el esquema.
- Preferí las APIs oficiales de Kotlin y Android. Mantené la lógica de negocio, los permisos, la integridad y la persistencia en NexusBack. Para llamadas HTTP, verificá primero el contrato real y seguí [`docs/nexusback/INTEGRATION_RULES.md`](docs/nexusback/INTEGRATION_RULES.md); no deduzcas endpoints o campos de una spec.

## Alcance y criterio

- Implementá únicamente la lógica necesaria para la tarea, con funciones breves y responsabilidades claras. Reutilizá patrones y comportamientos existentes antes de añadir otros.
- Evitá validaciones redundantes o defensivas para casos hipotéticos. No agregues interfaces, mappers, helpers, nuevas capas o dependencias sin una necesidad comprobada.
- Preservá contratos, comportamientos y funcionalidades existentes. No modifiques archivos ajenos a la tarea ni hagas refactorizaciones laterales sin una justificación ligada al objetivo.
- Ejecutá las verificaciones relacionadas, según [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md), y revisá el diff final. Informá cambios, pruebas realizadas, limitaciones y pendientes reales.

## Skills compartidas

- Para implementar o revisar código Kotlin, leé [`.agents/skills/android-code/SKILL.md`](.agents/skills/android-code/SKILL.md). Aplicala también junto con la skill especializada que corresponda.
- Para una funcionalidad nueva, leé [`.agents/skills/android-feature/SKILL.md`](.agents/skills/android-feature/SKILL.md).
- Para corregir, extender o modificar una funcionalidad existente, leé [`.agents/skills/android-change/SKILL.md`](.agents/skills/android-change/SKILL.md).

Codex usa este `AGENTS.md` y las rutas anteriores. Claude Code importa este archivo desde `CLAUDE.md`; para usar una skill compartida, abrí su `SKILL.md` por la ruta indicada, ya que `.agents/skills` no es su directorio de descubrimiento automático. Gemini CLI carga este archivo mediante `.gemini/settings.json` y descubre las skills del proyecto en `.agents/skills`. No mantengas copias de estas instrucciones por agente.
