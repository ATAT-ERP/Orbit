# Integración con NexusBack

NexusBack es la API del ERP A.T.A.T. y la fuente de verdad para operaciones de negocio, autorizaciones, integridad y persistencia. Orbit consume sus contratos HTTP y presenta el resultado. Supabase Auth provee la autenticación; Orbit enviará el JWT Bearer a los endpoints protegidos y NexusBack verificará identidad y permisos.

Las reglas comunes para implementar llamadas están en [INTEGRATION_RULES.md](INTEGRATION_RULES.md). Los contratos HTTP **utilizados por el móvil** se mantendrán en esta carpeta, en documentos por módulo cuando se haya verificado cada integración contra NexusBack: ruta, método, parámetros, JSON de solicitud y respuesta, autenticación y errores `NEX-*`. La documentación interna y la implementación del backend siguen siendo responsabilidad de NexusBack; aquí se registra solo la parte consumida por Orbit.

Actualmente Orbit no implementa llamadas HTTP ni tiene contratos de módulos verificados en este repositorio. No añadir descripciones de endpoints supuestos. Al integrar un módulo, confirmar primero el contrato real, implementar el cliente y actualizar su documento en el mismo cambio.
