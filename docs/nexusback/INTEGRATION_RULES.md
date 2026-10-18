# Reglas de integración con NexusBack

1. **Fuente de verdad.** NexusBack ejecuta las operaciones de negocio, valida integridad y permisos, y persiste datos. Orbit puede validar formato para ayudar al usuario, pero no sustituye ninguna decisión del servidor.
2. **Contrato verificado.** Consultar la implementación o documentación vigente de NexusBack antes de integrar un módulo. Respetar rutas, métodos HTTP, parámetros, estructuras JSON, tipos y códigos de estado reales. No inventar endpoints, campos, respuestas ni comportamientos por analogía.
3. **Autenticación.** Enviar `Authorization: Bearer <JWT>` en los endpoints protegidos, usando el token de la sesión de Supabase Auth. No incrustar tokens en el código ni registrarlos en logs. Dejar que NexusBack compruebe la identidad y los permisos.
4. **Nombres de campos.** Mantener los nombres del contrato en los DTO y en la serialización. Evitar transformaciones o mappers sin una diferencia concreta que lo requiera la UI.
5. **Errores.** Conservar el código funcional `NEX-*` recibido del servidor durante el manejo de errores. La UI puede traducirlo a un mensaje comprensible, pero no debe perderlo ni sustituirlo por un error genérico. Distinguir los errores funcionales de problemas de red o transporte.
6. **Sin reglas duplicadas.** No copiar en Orbit validaciones de negocio, cálculos oficiales ni permisos. Una restricción visual o validación de formato no implica autorización para ejecutar la operación.

Documentar cada contrato específico en esta carpeta solo después de verificarlo: incluir método y ruta, autenticación, solicitudes y respuestas reales, errores funcionales conocidos y la fuente o versión consultada. Mantener la documentación junto con cualquier cambio del consumo móvil. Véase [README.md](README.md) para el alcance de esta carpeta.
