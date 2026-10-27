# Navegación

## Objetivo y alcance

Proveer la navegación principal de Orbit entre las pantallas de acceso (Login) e inicio (Home), decidiendo el destino inicial según el estado de sesión y evitando el acceso a contenido protegido sin sesión válida. La selección de empresa y las funcionalidades internas de Company, Cloud o Billing quedan fuera de alcance de esta etapa.

## Decisión de Orbit: flujo y estados

1. Al abrir Orbit, mientras se restaura la sesión, se muestra un estado de carga simple y no se presenta ninguna pantalla de contenido.
2. Si hay sesión válida, la navegación arranca en Home.
3. Si no hay sesión válida, la navegación arranca en Login.
4. Al loguearse correctamente, se navega a Home y se elimina Login del historial (no se puede volver a Login con el botón Atrás tras loguearse).
5. Al cerrar sesión, se navega a Login y se limpia todo el historial de navegación (no se puede volver a Home con el botón Atrás tras cerrar sesión).

## Organización

- Las rutas están centralizadas en `ui/navigation/AppRoutes.kt` (objeto `Routes`, con `LOGIN` y `HOME`).
- El grafo de navegación está en `ui/navigation/AppNavigation.kt`, usando Navigation Compose.
- `MainActivity` es el punto de entrada: observa el estado de sesión y decide el destino inicial.

## Criterios de aceptación

- La app inicia en Home con sesión válida y en Login sin sesión.
- No se accede a Home sin sesión válida.
- El botón Atrás respeta el historial: no vuelve a Login tras loguearse, no vuelve a Home tras cerrar sesión.
- El proyecto compila.

## Excluidos o pendientes

- Selección de empresa: no implementada en esta etapa (restricción explícita del issue #8).
- Funcionalidades internas de Company, Cloud o Billing: fuera de alcance.
