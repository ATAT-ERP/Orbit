# Especificaciones funcionales de Orbit

Estas specs describen el comportamiento observable que Orbit debe ofrecer antes de implementar una funcionalidad. Son la referencia compartida para desarrollo y revisión. La arquitectura y las responsabilidades técnicas están en [`docs/ARCHITECTURE.md`](../docs/ARCHITECTURE.md); los contratos HTTP verificados que consuma Orbit se documentan en [`docs/nexusback/`](../docs/nexusback/README.md).

## Cómo escribir una spec

Usar solo los apartados que aporten información a la funcionalidad. Como guía común, cubrir:

- **Objetivo:** qué necesidad del usuario resuelve.
- **Contexto y alcance:** cuándo se usa y qué queda dentro.
- **Flujo principal:** pasos visibles para el usuario.
- **Comportamientos y estados:** resultados, carga, vacío y transiciones relevantes.
- **Errores y situaciones relevantes:** qué puede hacer el usuario ante cada fallo.
- **Dependencias o contratos externos:** servicios necesarios y dónde se verifica su contrato, sin duplicar sus payloads.
- **Criterios de aceptación:** escenarios observables y comprobables.
- **Excluidos o pendientes:** decisiones y contratos que aún no están cerrados.

Marcar explícitamente **Confirmado en NexusBack** para hechos comprobados en el backend, **Decisión de Orbit** para el comportamiento esperado del cliente y **Pendiente de verificar** para contratos o detalles de integración todavía no comprobados. Una decisión funcional no demuestra que una biblioteca o un servicio ya la soporte.

Antes de implementar, leer la spec correspondiente y verificar los contratos necesarios contra NexusBack. No interpretar funcionalidades futuras como existentes ni inventar rutas, campos, estados del backend o códigos de error. Mantener la spec actualizada cuando cambien decisiones o contratos. Si difiere del backend real, informar la discrepancia antes de implementar; no modificar NexusBack ni ampliar el alcance móvil solo para satisfacer una suposición documental.

## Specs iniciales

- [Autenticación](auth.md)
- [Selección de empresa](company.md)
