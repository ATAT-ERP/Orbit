# Perfil de usuario

## Objetivo y alcance

Permitir que el usuario autenticado consulte sus datos personales y modifique su nombre y apellido desde Orbit, usando el módulo Users de NexusBack. No incluye cambio de email, contraseña, foto de perfil, roles, permisos ni membresías.

## Flujo principal

1. Desde Home, el botón "Mi perfil" abre la pantalla. El botón Atrás de Android vuelve a Home.
2. Orbit consulta el perfil en NexusBack con la sesión vigente. Mientras espera muestra un estado de carga; si la consulta falla, muestra el error y permite reintentar.
3. El email se muestra como dato informativo (no editable). Nombre y apellido se muestran como campos editables.
4. "Guardar" envía los cambios y "Descartar" vuelve a los valores guardados. Ambos se habilitan solo si hay cambios y no hay un guardado en curso.

## Decisión de Orbit: comportamientos y estados

- Validación de presentación antes de enviar: se quitan los espacios al inicio y al final, nombre y apellido son obligatorios, y tienen un máximo de 150 caracteres (el mismo límite del backend). NexusBack vuelve a validar cada operación.
- Durante el guardado se bloquean los campos y los botones, y no se envía una segunda solicitud.
- Tras guardar correctamente, la pantalla muestra los datos devueltos por NexusBack.
- Si el guardado falla, se conserva lo escrito y se muestra el error. Un fallo de conexión se informa por separado de un error devuelto por NexusBack.
- La pantalla solo es accesible desde la navegación protegida, con una sesión válida.

## Dependencias

Contrato consumido: [`docs/nexusback/users.md`](../docs/nexusback/users.md).

## Criterios de aceptación

- El usuario puede consultar su información personal, obtenida desde NexusBack.
- Puede modificar nombre y apellido; la actualización se realiza mediante PATCH.
- Los datos modificados se muestran correctamente después del guardado y al volver a consultar el perfil.
- Se manejan los errores de conectividad y las respuestas de error del backend.
- No se permiten envíos duplicados.

## Verificaciones realizadas

- Emulador: consulta del perfil, edición y guardado, datos persistidos al volver a entrar, validación de campos vacíos, descartar cambios, y error de conexión al guardar con el backend detenido, con reintento exitoso.
- Tests unitarios: `ProfileViewModelTest` (carga inicial, validación sin llamada al backend, guardado exitoso, error del backend y prevención de envíos duplicados).

## Excluidos

- Modificación de email y contraseña, foto de perfil, roles, permisos y membresías.