# Archivos (cloud)

Contrato verificado contra el código de NexusBack en `main` (29/10/2026): `FileViewSet` en `apps/cloud/files/api/views.py`, `FileSerializer` y `ListQuerySerializer` en `apps/cloud/files/api/serializers.py`, `check_company_access` en `apps/cloud/files/services.py`, modelo `File` en `apps/cloud/models.py` y el enrutado de `apps/cloud/urls.py` con `apps/cloud/files/api/urls.py`. Listado y descarga probados desde Orbit contra NexusBack local.

## GET /api/cloud/files/

- **Auth:** `Authorization: Bearer <access_token>` (requerido).
- **Query:** `company_id` (uuid, requerido). El backend exige membresía del usuario en esa empresa. Admite además `q` y `category_id`, que Orbit no utiliza.
- **Response 200:** arreglo JSON sin paginación, ordenado por `created_at` descendente.
  ```json
  [
    {
      "id": "uuid",
      "company_id": "uuid",
      "name": "string",
      "original_name": "string",
      "mime_type": "string",
      "size": 0,
      "category_id": "uuid | null",
      "created_at": "2026-10-29T04:51:33.645669Z",
      "updated_at": "2026-10-29T04:51:33.645669Z"
    }
  ]
  ```
- `storage_key` no se expone. Las fechas llegan en ISO 8601 con zona UTC.

## GET /api/cloud/files/{id}/download/

- **Auth:** `Authorization: Bearer <access_token>` (requerido).
- **Parámetros:** ninguno. A diferencia del listado, no recibe `company_id`: autoriza por la membresía del usuario en la empresa dueña del archivo.
- **Response 200:**
  ```json
  { "url": "https://<proyecto>.supabase.co/storage/v1/object/sign/documents/<company_id>/<file_id>?token=...&download=<original_name>" }
  ```
- La URL es absoluta y **expira a los 60 segundos** (`SIGNED_URL_EXPIRATION_SECONDS`). Incluye el nombre original como nombre de descarga. Una empresa inactiva conserva la descarga de su histórico.

## Errores

- `400` con `NEX-DOC-001`: datos de la solicitud inválidos, por ejemplo `company_id` ausente o mal formado.
  ```json
  { "code": "NEX-DOC-001", "message": "Los datos enviados no son válidos.", "errors": { "campo": ["..."] } }
  ```
- `404` con `NEX-DOC-002`: archivo inexistente, o usuario sin membresía en la empresa. El backend responde lo mismo en ambos casos, de forma deliberada, para no revelar la existencia del archivo. También es la respuesta del listado cuando se pide una empresa ajena.
  ```json
  { "code": "NEX-DOC-002", "message": "Documento no encontrado." }
  ```
- `502` con `NEX-DOC-004`: no se pudo generar la URL firmada en el almacenamiento.
- `401`: sin Bearer o con Bearer inválido.

## Uso en Orbit

- `dto/CloudService.kt`: `getFiles(companyId)` y `getDownloadUrl(fileId)`.
- `dto/CloudRepository.kt`: expone ambas operaciones como `NetworkResult` mediante `SafeApiCall`.
- `dto/CloudFileDto.kt`: `id`, `name`, `original_name`, `mime_type`, `size` y `created_at`. `company_id`, `category_id` y `updated_at` llegan en la respuesta pero la pantalla no los usa.
- `dto/DownloadUrlDto.kt`: `url`.

## No consumido por Orbit

`POST /api/cloud/files/` (alta), `PATCH` (renombrado), `GET .../usage/` y los endpoints de hojas de XLSX existen en el backend pero no se usan desde el móvil. No se documentan acá hasta que se integren.