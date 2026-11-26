# SIGAC-UTA

Sistema Inteligente de Gestión Académica y Conectividad. Prototipo Java para el examen práctico integrador de Estructura de Datos de la Universidad Técnica de Ambato, FISEI, Ingeniería en Software, tercer nivel.

## Compilación y ejecución

Se requiere un **JDK 17 o posterior** con `javac` y `java` disponibles. Ejecutar los comandos desde la carpeta del proyecto, que contiene `fuentes.txt`. El proyecto usa únicamente Java estándar. La verificación de la entrega se realizó con JDK 21, compilando con `--release 17` y `-Xlint:all`.

**Windows PowerShell**

```powershell
javac '@fuentes.txt'
java -cp out app.Main
```

**Windows CMD, Bash y Zsh**

```sh
javac @fuentes.txt
java -cp out app.Main
```

El menú abre una sesión en memoria con la jerarquía base **UTA → FISEI → Ingeniería en Software**, con el código de carrera `SOF`. Los datos de la sesión se pierden al salir.

Para reproducir el caso oficial, seleccionar la opción **6. Ejecutar caso integrador**. Esta opción usa su propio conjunto de datos y conserva la sesión de trabajo. También se puede ejecutar directamente junto con la suite de pruebas.

```sh
java -cp out app.Main --demo
java -cp out pruebas.PruebasSigac
```

La suite informa el resultado de cada caso y termina con código de salida 1 si existe un fallo.

## Operaciones del menú

| Opción | Operaciones |
|---|---|
| 1. Estudiantes | Registrar, listar, comparar búsquedas por matrícula, actualizar el nombre y eliminar. |
| 2. Atención e historial | Encolar, listar pendientes, atender por orden de llegada y deshacer el último cambio de nombre. |
| 3. Organización académica y ABB | Mostrar la jerarquía, registrar facultad y carrera, y ejecutar preorden, inorden y posorden. |
| 4. Red de laboratorios | Registrar y eliminar equipos, listar y administrar conexiones ponderadas, ejecutar BFS y DFS y mostrar equipos no alcanzables. |
| 5. Amistades y recomendaciones | Registrar y quitar amistades, consultar amigos directos y recomendar colaboradores de segundo grado. |
| 6. Caso integrador | Ejecutar el escenario oficial completo desde un estado inicial reproducible. |

## Reglas del caso integrador

- Se insertan las matrículas `50, 30, 70, 20, 40, 60, 80`, en ese orden. El ABB elimina un nodo con dos hijos usando el **sucesor inorden**, que es el mínimo del subárbol derecho. El sucesor de `30` es `40`.
- La red de laboratorio usa **aristas dirigidas** y pesos enteros positivos. Las conexiones se insertan en el orden de la tabla. Cada recorrido visita los vecinos en el orden en que se agregaron. La lista de equipos no alcanzables conserva el orden de registro de equipos. La alcanzabilidad se calcula desde el origen seleccionado.
- La red social usa amistades recíprocas. Por cada amigo directo se examinan sus vecinos. El usuario y sus amigos directos quedan excluidos. Los candidatos se ordenan por cantidad descendente de amigos en común y los empates por matrícula ascendente.
- El vector, la lista, el ABB y las hojas de la jerarquía comparten cada estudiante. La baja retira la matrícula de esas estructuras y del grafo social. Si un estudiante o equipo tiene una solicitud pendiente, primero se debe atender esa solicitud. Los IDs de las solicitudes son únicos durante la sesión, incluso después de atenderlas.
- El historial guarda cambios efectivos de nombre con su valor anterior. Al eliminar un estudiante, se retiran sus cambios pendientes de la pila, conservando el orden de las demás entradas.

| Inserción | Origen | Destino | Peso |
|---|---|---|---:|
| 1 | PC1 | SW1 | 2 |
| 2 | SW1 | SRV1 | 3 |
| 3 | SRV1 | PC2 | 2 |
| 4 | PC1 | PC3 | 1 |

Orden de registro de equipos: `PC1, SW1, SRV1, PC2, PC3, PC4`. `PC4` queda aislado. Las amistades del escenario son `Ana–Beto`, `Ana–Carla`, `Beto–Diego`, `Carla–Diego` y `Carla–Elena`.

| Verificación | Resultado del caso |
|---|---|
| Inorden inicial | `[20, 30, 40, 50, 60, 70, 80]` |
| Inorden después de eliminar 30 | `[20, 40, 50, 60, 70, 80]` |
| Búsqueda de 40 previa a la baja | 5 comparaciones secuenciales y 3 en el ABB |
| Atención FIFO | `S1 → S2 → S3` |
| Reversión LIFO | La matrícula 80 vuelve a «Nombre corregido» y después a «Estudiante 80» |
| BFS desde PC1 | `[PC1, SW1, PC3, SRV1, PC2]`. No alcanzable: `[PC4]`. |
| DFS desde PC1 | `[PC1, SW1, SRV1, PC2, PC3]`. No alcanzable: `[PC4]`. |
| Recomendaciones para Ana | Diego con 2 amigos en común y Elena con 1 |
| BFS después de retirar `SW1 → SRV1` | `[PC1, SW1, PC3]`. No alcanzables: `[SRV1, PC2, PC4]`. |

## Complejidad temporal

| Operación | Complejidad | Motivo |
|---|---|---|
| Obtener elemento del vector por índice | O(1) | Acceso directo al arreglo. |
| Insertar al final del vector | O(1) amortizado, O(n) al ampliar | La capacidad se duplica al llenarse. |
| Buscar matrícula en la lista | O(n) | Puede visitar todos los estudiantes. Cada nodo examinado cuenta como una comparación. |
| Buscar, insertar o eliminar una clave del ABB | O(h), O(n) en el peor caso | Se recorre un camino de altura h en un árbol sin balanceo. La baja con dos hijos incluye la búsqueda del sucesor. |
| Recorrer el ABB | O(n) | Se producen las n claves mediante recorrido iterativo. |
| Encolar y desencolar | O(1) | La cola conserva referencias al primer y último nodo. El registro completo de la solicitud agrega las búsquedas y validaciones del servicio. |
| Apilar y desapilar | O(1) amortizado al apilar, O(1) al desapilar | Se opera sobre el último índice del vector. |
| BFS y DFS | O(V + E) | Listas de adyacencia y marcas por índice. La búsqueda inicial del origen y la lista de no alcanzables cuestan O(V). |
| Eliminar vértice | O(V + E) | Se quita el vértice, se limpian sus aristas entrantes y se actualizan las posiciones para las marcas de visita. |
| Recomendar contactos | O(V + A + k·h + k²) | A es la suma de los grados de los amigos directos, k los candidatos y h la altura del ABB. El conteo usa marcas indexadas, la resolución de cada candidato usa el ABB y el orden final usa inserción. |

## Organización y evidencia

| Ruta | Contenido |
|---|---|
| `src/modelo` | Estudiante y solicitud de laboratorio. |
| `src/estructuras` | Vector, lista, pila, cola, árbol general, ABB y grafo implementados manualmente. |
| `src/servicios` | Gestión académica, atención, historial, grafos y coordinación entre módulos. |
| `src/app` | Punto de entrada, menú y escenario oficial. |
| `src/pruebas` | Suite ejecutable de pruebas normales, extremas, erróneas e integradas. |
| [Matriz de pruebas](docs/matriz-pruebas.md) | Resultados observados, comandos y capturas de salida. |
| [Contribuciones](docs/contribuciones.md) | Commits locales verificables e instrucciones de publicación en GitHub. |
| [Comprobación individual](docs/defensa.md) | Explicaciones de las cuatro preguntas previstas en el examen. |
