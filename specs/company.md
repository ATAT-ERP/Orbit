# Selección de empresa

## Objetivo y alcance

Permitir que el usuario autenticado elija una empresa autorizada y activa como contexto de trabajo para las consultas posteriores de Orbit. Esta primera versión cubre consulta y selección; no incluye crear, editar, buscar ni administrar empresas o miembros.

## Flujo principal

1. Con una sesión utilizable, Orbit consulta las empresas activas a las que pertenece el usuario. Mientras espera, indica la carga y no permite continuar con una empresa sin validar.
2. Si hay empresas, muestra sus nombres y permite seleccionar una. La selección queda visible como empresa activa.
3. Al entrar a una funcionalidad que requiere empresa, Orbit usa la empresa activa. Si aún no existe una selección válida, solicita elegir una antes de realizar la consulta.
4. El usuario puede volver a la selección para cambiar la empresa activa (sin cerrar sesión); las consultas posteriores usan la nueva selección.

## Decisión de Orbit: comportamientos y estados

- Solo se ofrecen para seleccionar empresas activas devueltas para el usuario autenticado. La selección no concede acceso: cada operación posterior debe ser autorizada por NexusBack.
- La empresa activa es un estado de Orbit (en memoria, compartido), no modifica las membresías ni fija un contexto global en NexusBack.
- No se elige automáticamente una empresa aunque la lista contenga una sola. La elección explícita evita usar un contexto no advertido por el usuario.
- Si el listado está vacío, se muestra un estado vacío claro. Las funciones que requieren empresa permanecen inaccesibles hasta contar con una selección válida.
- Durante la carga inicial o una recarga, no se interpreta la ausencia de respuesta como lista vacía. Un fallo conserva un estado de error con opción de reintentar.
- La empresa activa se mantiene mientras la app está en uso. Al volver a abrir Orbit se pide elegirla de nuevo, incluso si la sesión de autenticación se recuperó. También se limpia al cerrar sesión.

## Errores y situaciones relevantes

- Fallo de red o del servicio al listar: explicar que no se pudieron cargar las empresas, ofrecer reintento y no permitir una nueva selección basada en datos incompletos.
- Sesión inválida al consultar: aplicar el flujo de [autenticación](auth.md), sin mostrar empresas protegidas.
- Error funcional de NexusBack: mostrar una explicación comprensible y conservar el código original para diagnóstico, según las reglas de integración.

## Confirmado en NexusBack

- `GET /api/companies/` requiere Bearer, devuelve solo empresas con membresía del usuario y, sin filtros, únicamente las activas. Un Bearer ausente o inválido se rechaza con `NEX-USR-010`.
- Cada resultado del listado incluye: `id`, `my_role`, `type`, `name`, `legal_name`, `tax_id`, `email`, `phone`, dirección completa, `is_active`, `created_at`, `updated_at`. Orbit, en esta primera versión, solo consume `id` y `name` para identificar y seleccionar; el resto queda disponible para cuando se necesite.
- NexusBack es la fuente de verdad para empresas, membresías y permisos. Las rutas verificadas no ofrecen una operación para fijar una «empresa activa» global.

Fuentes verificadas en NexusBack: `apps/company/api/views.py`, `apps/company/api/urls.py`, `apps/company/api/serializers.py`, `apps/company/models/company.py`.

## Implementación

- DTO y acceso a datos: `dto/CompanyDto.kt`, `dto/CompanyService.kt`, `dto/CompanyRepository.kt` (mismo patrón que `UserRepository`). El manejo de errores de red se comparte en `dto/SafeApiCall.kt`.
- Estado compartido de la empresa activa: `dto/ActiveCompany.kt` (un único objeto observable, sin duplicarse en varios ViewModels).
- Pantalla: `ui/company/CompanySelectionScreen.kt` + `ui/company/CompanySelectionViewModel.kt`, con estados de carga, contenido, vacío y error.
- Navegación: `Routes.COMPANY_SELECTION` en el flujo Login → Selección de empresa → Home. Desde Home, el botón "Cambiar de empresa" vuelve a la selección sin cerrar sesión.

## Criterios de aceptación

- Un usuario autenticado ve únicamente las empresas activas autorizadas que devuelve NexusBack y puede elegir una.
- Sin empresas disponibles, ve el estado vacío y no puede iniciar consultas que requieren empresa.
- Durante una carga o un error de listado, no se muestra un vacío engañoso; el usuario puede reintentar después de un fallo.
- Es posible cambiar de empresa sin cerrar sesión.
- El cierre de sesión limpia la empresa activa.

## Pendiente de verificar y excluidos

- Persistir la empresa elegida entre aperturas queda fuera de esta primera versión.
- Fuera de alcance: alta, edición, baja, búsqueda, filtros avanzados y administración de miembros de empresas (aunque el endpoint de alta ya existe en NexusBack y se usó solo para generar datos de prueba).
