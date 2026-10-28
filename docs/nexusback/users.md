# Perfil propio (users)

Contrato verificado contra el código de NexusBack en `main` (28/10/2026): acción `me` de `UserViewSet` en `apps/users/api/views.py`, `UserSerializer` en `apps/users/api/serializers.py`, modelo `User` en `apps/users/models.py` y `docs/modules/USERS.md`. GET y PATCH probados desde Orbit contra NexusBack local.

## GET /api/users/me/

- **Auth:** `Authorization: Bearer <access_token>` (requerido). La identidad sale del Bearer, no de un UUID enviado por el cliente.
- **Response 200:**
  ```json
  {
    "id": "uuid",
    "email": "string | null",
    "first_name": "string",
    "last_name": "string",
    "avatar_path": "string | null",
    "is_active": true,
    "is_system_admin": false
  }
  ```

## PATCH /api/users/me/

- **Auth:** `Authorization: Bearer <access_token>` (requerido).
- **Request:** solo se pueden modificar `first_name` y `last_name`. Los campos omitidos conservan su valor.
  ```json
  { "first_name": "string", "last_name": "string" }
  ```
- **Response 200:** el perfil actualizado, con el mismo formato que el GET.
- **Restricciones del backend:** `first_name` y `last_name` admiten hasta 150 caracteres.

## Errores

- `400` con `NEX-USR-003` y detalle por campo en `errors`: dato inválido o campo protegido (`id`, `email`, `avatar_path`, `is_active`, `is_system_admin` o cualquier otro). Documentado por NexusBack; no reproducido desde Orbit, porque la app solo envía nombre y apellido validados.
  ```json
  { "code": "NEX-USR-003", "message": "Los datos enviados no son válidos.", "errors": { "campo": ["..."] } }
  ```
- `401`: sin Bearer o con Bearer inválido (`NEX-USR-010` verificado para Bearer inválido).
- `403`: sin permisos o perfil inactivo.

## Uso en Orbit

- `dto/UserService.kt`: `getCurrentUser()` y `updateCurrentUser()`.
- `dto/UserRepository.kt`: expone ambas operaciones como `NetworkResult` mediante `SafeApiCall`.
- `dto/UserDto.kt`: `UserDto` (respuesta) y `UserUpdateDto` (solo nombre y apellido).