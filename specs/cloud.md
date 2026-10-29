# Archivos (Cloud)

## Objetivo y alcance

Permitir que el usuario autenticado consulte y descargue los archivos de su empresa activa desde Orbit, usando el módulo Cloud de NexusBack. No incluye subida, edición, borrado, categorías, búsqueda, filtros ni visores de PDF, Excel u otros formatos.

## Flujo principal

1. Desde Home, el botón "Archivos" abre la pantalla. El botón Atrás de Android vuelve a Home.
2. Orbit consulta los archivos de la empresa activa en NexusBack. Mientras espera muestra un estado de carga; si la consulta falla, muestra el error y permite reintentar.
3. Cada archivo se presenta con su nombre, su tamaño y su fecha de creación.
4. Al tocar un archivo, Orbit pide la URL de descarga a NexusBack y delega la descarga al sistema Android, que guarda el archivo en la carpeta Descargas.
5. La pantalla informa el inicio de la descarga, o el error si no pudo prepararse.

## Decisión de Orbit: comportamientos y estados

- La pantalla observa la empresa activa. Al cambiar de empresa el listado se vuelve a pedir, pasando antes por el estado de carga, de modo que nunca se muestran archivos de la empresa anterior.
- Estados del listado: carga, contenido, vacío y error. El error ofrece reintentar.
- `NEX-DOC-002` en el listado se presenta como falta de acceso a los archivos de la empresa, porque el mensaje literal del backend ("Documento no encontrado") no describe la situación en un listado. El código recibido se usa para elegir el mensaje, no se reemplaza por un error genérico.
- Un fallo de conexión se informa por separado de un error devuelto por NexusBack.
- La URL firmada se pide recién al tocar el archivo, se entrega al sistema y se descarta. No se guarda ni se reutiliza, porque expira al minuto.
- La descarga se delega a `DownloadManager`, el servicio del sistema. Orbit no implementa la transferencia ni accede al almacenamiento de NexusBack por fuera de la URL firmada.
- El archivo se guarda con su nombre original en la carpeta Descargas del dispositivo.
- La pantalla solo es accesible desde la navegación protegida, con una sesión válida y una empresa activa.

## Dependencias

Contrato consumido: [`docs/nexusback/cloud.md`](../docs/nexusback/cloud.md).
Empresa activa: `dto/ActiveCompany.kt`, establecida en la selección de empresa ([`company.md`](company.md)).

## Criterios de aceptación

- El usuario puede consultar los archivos de su empresa activa.
- El listado respeta las autorizaciones de NexusBack; no se muestran archivos de empresas no autorizadas.
- Es posible descargar un archivo mediante el flujo autorizado, y el usuario recibe información del resultado.
- Se manejan los errores de conectividad, las respuestas de error del backend y los listados vacíos.
- Al cambiar de empresa se actualiza el contenido.

## Verificaciones realizadas

- Emulador: listado de una empresa con archivos, empresa sin archivos, descarga de un PDF y apertura del archivo descargado para comprobar su integridad, cambio de empresa, error de conexión en el listado con reintento exitoso, y error al preparar la descarga con el backend detenido.
- Tests unitarios: `CloudViewModelTest` (listado con contenido, listado vacío, acceso no autorizado, cambio de empresa, descarga exitosa y fallo de red al descargar).

## Excluidos

- Subida, edición y borrado de archivos.
- Categorías, búsqueda y filtros del listado, previstos en otra issue.
- Visores de contenido y lectura de hojas de archivos XLSX.
- Copia local del Cloud y almacenamiento propio.

## Permisos de Android

`WRITE_EXTERNAL_STORAGE` declarado con `maxSdkVersion="28"`: `DownloadManager` lo exige para escribir en la carpeta Descargas en Android 8 y 9. Desde Android 10 no se requiere ningún permiso, y Orbit no lo solicita.