# Autenticación

## Objetivo y alcance

Permitir el ingreso con email y contraseña de una cuenta existente, recuperar una sesión utilizable al abrir Orbit, proteger el acceso y cerrar la sesión. Registro, recuperación y cambio de contraseña e ingreso social quedan fuera de alcance.

## Confirmado en NexusBack

- Supabase Auth es el proveedor de identidad y sesiones. NexusBack tiene `POST /api/users/login/`: autentica email y contraseña mediante Supabase, comprueba el perfil local y devuelve los tokens de la sesión cuando el ingreso es aceptado.
- Los endpoints protegidos reciben el access token en `Authorization: Bearer <JWT>`. NexusBack valida el token ante Supabase y comprueba el estado del usuario y sus permisos. Tener una sesión local de Supabase no garantiza autorización en NexusBack.
- Existe `POST /api/users/logout/`: requiere Bearer, cierra en Supabase la sesión representada por ese token y responde `204` si se completa. Un access token ya emitido puede seguir vigente hasta su expiración; el cliente debe descartar sus tokens.
- Los errores de login comprobados incluyen datos inválidos (`NEX-USR-003`), límite de solicitudes (`NEX-USR-007`), credenciales inválidas (`NEX-USR-008`), fallo del ingreso (`NEX-USR-009`) y perfil inactivo (`NEX-USR-002`). Un Bearer mal formado o que falla la validación produce `NEX-USR-010` en los endpoints protegidos. Sin Bearer, se rechaza el acceso, pero el código de error no está confirmado como `NEX-USR-010`.
- NexusBack no expone hoy un endpoint propio de refresh.

Fuentes verificadas en NexusBack, revisión `13abaa7`: `apps/users/api/urls.py`, `apps/users/api/views.py`, `apps/users/authentication.py`, `docs/modules/USERS.md` y `docs/ERROR_CODES.md`. Los contratos consumidos por Orbit se registrarán en [`docs/nexusback/`](../docs/nexusback/README.md) al implementar la integración.

## Decisión de Orbit: flujo y estados

1. Al abrir Orbit, comprobar si el proveedor de autenticación puede recuperar una sesión. Mostrar carga mientras se determina el resultado, sin presentar contenido protegido.
2. Si no hay sesión válida o recuperable, solicitar email y contraseña. Durante el ingreso, mostrar carga e impedir envíos duplicados. Si se acepta, avanzar a la selección de empresa; si se rechaza, permanecer en el ingreso y permitir corregir o reintentar.
3. Si se recupera una sesión, usar su access token para consultar NexusBack cuando corresponda. El acceso protegido depende también de la respuesta de NexusBack. Un rechazo por permisos se presenta como tal; no se convierte automáticamente en sesión expirada.
4. Si el access token deja de ser válido, usar únicamente el mecanismo oficial de Supabase que se verifique para intentar recuperar la sesión. Si no se consigue, retirar el acceso protegido y solicitar un nuevo ingreso. Evitar ciclos de reintento.
5. Al solicitar cierre de sesión, finalizar inmediatamente el acceso local, limpiar el estado autenticado y la empresa activa, y volver al acceso público. Informar por separado si una revocación remota falla; no comunicarla como exitosa.

Una navegación directa a una pantalla protegida sin acceso válido lleva al ingreso. Tras autenticarse, el usuario continúa el flujo correspondiente y selecciona empresa si la funcionalidad la requiere. No se puede volver a contenido protegido mediante navegación hacia atrás después de cerrar o perder la sesión.

## Decisión de Orbit: errores

- Datos de ingreso incompletos o email inválido: indicar qué corregir. Credenciales rechazadas: mostrar un mensaje comprensible y conservar el email para otro intento.
- Fallo de Supabase, falta de conexión y error de NexusBack: distinguir su origen y ofrecer una acción útil. Un problema de conexión durante la recuperación no demuestra que la sesión sea inválida; permitir reintentar sin mostrar contenido protegido.
- Conservar los códigos `NEX-*` realmente devueltos por NexusBack. No asignar códigos `NEX-*` a errores directos de Supabase ni crear códigos supuestos.
- Un fallo de revocación remota se informa aun cuando Orbit ya haya retirado el acceso local.

## Criterios de aceptación

- Con credenciales válidas y perfil autorizado, el usuario accede al flujo protegido; con credenciales inválidas o perfil rechazado, no accede y ve el motivo o una indicación útil.
- Al reabrir Orbit, una sesión recuperable evita un nuevo ingreso; una sesión no recuperable lleva al ingreso. Durante la comprobación no aparece contenido protegido.
- Un error de conexión no se presenta como sesión inválida. Un rechazo de permisos de NexusBack no se trata como expiración del token.
- Cuando no es posible recuperar una credencial válida, las pantallas protegidas dejan de estar disponibles.
- Tras solicitar logout, el acceso local y la empresa activa desaparecen; un fallo remoto se informa sin afirmar que la revocación tuvo éxito.

## Pendiente de verificar

- Biblioteca oficial de Supabase compatible con Android y mecanismos concretos para restaurar, almacenar y renovar la sesión. Orbit no implementará renovación propia del JWT ni asumirá renovación automática.
- Compatibilidad entre los tokens entregados por el login existente de NexusBack y la sesión que administrará la biblioteca Android elegida. Si no pueden incorporarse, resolver explícitamente la vía de ingreso y su validación frente al perfil de NexusBack antes de implementar.
- Comportamiento de la biblioteca ante fallos de cierre: confirmar que puede garantizarse la limpieza local y cómo se trata un refresh token cuando la revocación remota falla.
- Formato exacto del rechazo sin Bearer: `docs/modules/USERS.md` lo agrupa con `NEX-USR-010`, pero `SupabaseBearerAuthentication.authenticate` devuelve `None` cuando falta el header. Verificar la respuesta efectiva antes de documentar el código consumido por Orbit.
