# Selección de empresa

## Objetivo y alcance

Permitir que el usuario autenticado elija una empresa autorizada y activa como contexto de trabajo para las consultas posteriores de Orbit. Esta primera versión cubre consulta y selección; no incluye crear, editar, buscar ni administrar empresas o miembros.

## Flujo principal

1. Con una sesión utilizable, Orbit consulta las empresas activas a las que pertenece el usuario. Mientras espera, indica la carga y no permite continuar con una empresa sin validar.
2. Si hay empresas, muestra sus nombres y permite seleccionar una. La selección queda visible como empresa activa.
3. Al entrar a una funcionalidad que requiere empresa, Orbit usa la empresa activa. Si aún no existe una selección válida, solicita elegir una antes de realizar la consulta.
4. El usuario puede volver a la selección para cambiar la empresa activa; las consultas posteriores usan la nueva selección.

## Decisión de Orbit: comportamientos y estados

- Solo se ofrecen para seleccionar empresas activas devueltas para el usuario autenticado. La selección no concede acceso: cada operación posterior debe ser autorizada por NexusBack.
- La empresa activa es un estado de Orbit; elegirla no modifica las membresías ni fija un contexto global en NexusBack.
- No se elige automáticamente una empresa aunque la lista contenga una sola. La elección explícita evita usar un contexto no advertido por el usuario.
- Si el listado está vacío, se muestra un estado vacío claro y la opción de volver a cargar. Las funciones que requieren empresa permanecen inaccesibles hasta contar con una selección válida.
- Durante la carga inicial o una recarga, no se interpreta la ausencia de respuesta como lista vacía. Un fallo conserva un estado de error con opción de reintentar.
- La empresa activa se mantiene mientras la app está en uso. Al volver a abrir Orbit se pide elegirla de nuevo, incluso si la sesión de autenticación se recuperó. También se limpia al cerrar o perder la sesión. Al recargar la lista, si la empresa activa ya no aparece entre las disponibles, se descarta y se solicita elegir otra antes de continuar.
- Después de cambiar de empresa, los datos mostrados que pertenecían a la anterior dejan de presentarse como si correspondieran a la nueva; las consultas siguientes se hacen en el nuevo contexto.

## Errores y situaciones relevantes

- Fallo de red o del servicio al listar: explicar que no se pudieron cargar las empresas, ofrecer reintento y no permitir una nueva selección basada en datos incompletos.
- Sesión inválida al consultar: aplicar el flujo de [autenticación](auth.md), sin mostrar empresas protegidas.
- Empresa seleccionada que deja de estar autorizada o disponible: comprobar de nuevo las empresas accesibles; si ya no aparece, quitarla como contexto activo e informar que debe elegirse otra. Un rechazo de una operación particular no basta por sí solo para concluir que se perdió toda la membresía.
- Error funcional de NexusBack: mostrar una explicación comprensible y conservar el código original para diagnóstico, según las reglas de integración.

## Confirmado en NexusBack

- `GET /api/companies/` requiere Bearer, devuelve solo empresas con membresía del usuario y, sin filtros, únicamente las activas. Cada resultado incluye identificador y nombre. Un Bearer ausente o inválido se rechaza; un detalle de empresa ajena devuelve `404` con `NEX-COM-004`.
- NexusBack es la fuente de verdad para empresas, membresías y permisos. Las rutas verificadas no ofrecen una operación para fijar una «empresa activa» global.

Fuentes verificadas en NexusBack, revisión `13abaa7`: `apps/company/api/views.py`, `apps/company/api/urls.py`, `apps/company/tests/test_list.py`, `apps/company/tests/test_permissions.py` y `docs/modules/COMPANY.md`. El contrato consumido por Orbit se registrará en [`docs/nexusback/`](../docs/nexusback/README.md) al integrar la funcionalidad.

## Criterios de aceptación

- Un usuario autenticado ve únicamente las empresas activas autorizadas que devuelve NexusBack y puede elegir una.
- Sin empresas disponibles, ve el estado vacío y no puede iniciar consultas que requieren empresa.
- Durante una carga o un error de listado, no se muestra un vacío engañoso; el usuario puede reintentar después de un fallo.
- Las consultas que requieren empresa usan la selección vigente; al cambiarla, no se muestran datos de la anterior como datos actuales.
- Si la selección deja de estar disponible o termina la sesión, se retira y se vuelve a requerir una selección válida.

## Pendiente de verificar y excluidos

- Persistir la empresa elegida entre aperturas queda fuera de esta primera versión; requeriría definir y verificar una revalidación al restaurarla.
- **Pendiente de verificar por módulo:** qué consultas posteriores requieren empresa y cómo recibe cada una su identificador. Se documentará al definir su alcance y verificar su contrato.
- Fuera de alcance: alta, edición, baja, búsqueda, filtros avanzados y administración de miembros de empresas.
