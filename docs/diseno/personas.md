# Diseño de interfaz · Área 1: Personas (`PersonasScreen`)

## Boceto de referencia
Se toma el boceto de la guía de la sección 1 (Pantalla *Personas de la casa*), con las partes numeradas del 1 al 8:
1. Barra superior: título «Personas de la casa» y debajo «N personas registradas».
2. Dos pestañas internas: «Personas cuidadas» y «Cuidadores».
3. Tarjetas desplazables por persona.
4. Círculo con iniciales pintado con el color de perfil (o gris `#9E9E9E` si no tiene).
5. Etiquetas de papeles: «Recibe cuidados» y/o «Cuidador/a».
6. Notas y «La cuidan: …» con los nombres de sus cuidadores.
7. Contacto principal (parentesco y nombre) y botón «Llamar» (abre el marcador con el número).
8. Menú inferior ya existente (no se modifica).

## Componentes de Compose a utilizar
- `Scaffold` y `TopAppBar`
- `TabRow` / `PrimaryTabRow` y `Tab`
- `LazyColumn`
- `Card`
- `Box`, `Column`, `Row` y `Spacer`
- `Text` y `Icon` / `TextButton` / `AssistChip` o `FilterChip`