# Autenticación (users)

Contrato verificado contra NexusBack corriendo localmente (`docker compose up --build`,
rama `uziel` de Orbit, commit del backend en `main` al momento de la verificación:
22/10/2026). Cubre login, logout y acceso a un endpoint protegido con el Bearer token.

## POST /api/users/login/

- **Auth:** ninguna (público).
- **Request:**
  ```json
  { "email": "string", "password": "string" }
  ```
- **Response 200:**
  ```json
  {
    "id": "uuid",
    "access_token": "string (JWT)",
    "refresh_token": "string",
    "expires_in": 3600,
    "token_type": "bearer"
  }
  ```
  Los tokens son los que emite Supabase Auth; NexusBack no emite un token propio.
- **Errores funcionales verificados:**
  - `NEX-USR-008` (401) — credenciales inválidas.
  - Ver también `NEX-USR-003` (datos inválidos) y `NEX-USR-007` (rate limit), no
    reproducidos en esta verificación pero documentados por NexusBack.

## POST /api/users/logout/

- **Auth:** `Authorization: Bearer <access_token>` (requerido).
- **Request:** sin body.
- **Response 204:** sin cuerpo. Revoca esa sesión puntual en Supabase (`scope=local`).
- Verificado con Bearer válido → `204`.

## Acceso con Bearer a un endpoint protegido (ejemplo: GET /api/companies/)

Usado para confirmar que el Bearer token recibido del login se transmite y valida
correctamente en cualquier endpoint protegido de NexusBack (`SupabaseBearerAuthentication`
+ `IsAuthenticated`), no es un contrato específico de auth sino la verificación de que
el mecanismo funciona de punta a punta.

- Con Bearer válido → `200` (lista de companies del usuario).
- Sin Bearer o Bearer vacío → `401` con `NEX-USR-010` (Bearer mal formado o inválido
  contra Supabase).

## Notas de entorno para pruebas locales

- El emulador Android no resuelve `localhost`; la app debe apuntar a `http://10.0.2.2:8000/api/`.
- NexusBack corriendo en Docker necesita `10.0.2.2` agregado a `DJANGO_ALLOWED_HOSTS` en
  `.env`, si no rechaza el request con `DisallowedHost`.
- El manifest de Orbit necesita `android.permission.INTERNET` y
  `android:usesCleartextTraffic="true"` (el backend local corre sobre HTTP, no HTTPS).

## Pendiente — limitación conocida

**Persistencia de sesión al reabrir la app:** no confirmada. Tras loguearse y cerrar la
app por completo, al reabrirla el SDK de Supabase (`gotrue-kt` 2.1.0) no recupera la
sesión — `MainActivity` vuelve a mostrar `LoginScreen`. La configuración actual de
`SupabaseClient` (`install(Auth)` sin opciones) usa los valores por defecto del SDK
(`autoSaveToStorage` y `autoLoadFromStorage` en `true`), que en teoría deberían persistir
la sesión importada con `importSession()`. No se identificó la causa raíz verificando
solo con la versión instalada; puede requerir configurar explícitamente un `SessionManager`
o revisar el comportamiento de `importSession()` en esta versión del SDK. Queda para una
próxima iteración, no evaluado más a fondo para no introducir configuración de SDK sin
confirmar primero el enfoque.

**Fuente consultada:** código real de NexusBack (`apps/company/api/views.py`,
`apps/users/api/urls.py`, respuestas verificadas con `curl`/`Invoke-RestMethod` contra
el servidor local), no documentación externa.
