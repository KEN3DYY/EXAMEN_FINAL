# Matriz de pruebas de SIGAC-UTA

La ejecución con JDK 21, compilada para Java 17, produjo **31 pruebas aprobadas y 0 fallidas**. Cada ID corresponde a un caso de `src/pruebas/PruebasSigac.java`. El [registro íntegro](evidencias/salida-pruebas.txt) conserva la línea de resultado de cada caso.

## Resultados verificados

| ID | Módulo | Tipo | Entrada o acción | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|---|
| P01 | M1, M3 | Normal | Alta de 12, cambio de nombre, consulta y baja | Mismo nombre editado en búsquedas y jerarquía. Baja en todas las vistas. | Aprobada. Ambas búsquedas devolvieron a Isabel Lopez. Vector y carrera quedaron vacíos. |
| P02 | M1 | Errónea | Repetir 50 e intentar borrar 99 | Ambos rechazos preservan los siete registros. | Aprobada. Matrícula 50 sigue siendo Ana y la jerarquía conserva siete hojas. |
| P03 | M1 | Errónea | Nombre nulo o vacío, carrera nula, IDs 0 y −1 | Rechazo de las entradas inválidas sin crear registros. | Aprobada. Vector y ABB quedaron vacíos. |
| P04 | M1, M3 | Normal | Buscar 40 y 99 con los siete datos oficiales | Para 40: 5 comparaciones secuenciales y 3 en ABB. Para 99: 7 y 3, sin coincidencias. | Aprobada. Se verificaron los cuatro conteos y el resultado de cada método. |
| P05 | M1 | Extrema | Quitar cabeza, posición intermedia, cola y único nodo de una lista | Enlaces válidos y posibilidad de insertar tras vaciar. | Aprobada. Después de vaciar y reinsertar se obtuvo `5, 6`. |
| P06 | M1 | Extrema | Insertar 20 valores, borrar índice 10 y consultar el límite | Redimensionar y desplazar conservando índices válidos. | Aprobada. El valor 11 pasó al índice 10 y el índice 19 quedó fuera de rango. |
| P07 | M3 | Normal | Ejecutar los tres recorridos del ABB oficial | Preorden `[50,30,20,40,70,60,80]`, inorden `[20,30,40,50,60,70,80]` y posorden `[20,40,30,60,80,70,50]`. | Aprobada. Coincidieron los tres recorridos. |
| P08 | M1, M3, M5 | Integración | Eliminar 30 del escenario oficial | Inorden `[20,40,50,60,70,80]` y ausencia de 30 en las estructuras vinculadas. | Aprobada. Vector, lista, ABB, jerarquía y grafo social quedaron sincronizados. |
| P09 | M3 | Extrema | Eliminar raíz 50 con sucesor 60, que tiene hijo derecho 70 | Reubicar el sucesor y conservar 70. | Aprobada. Inorden `[30,60,70,80,90]`. |
| P10 | M3 | Extrema | ABB vacío y bajas de una raíz con un hijo y de un nodo único | Consultas vacías seguras y reubicación correcta del hijo. | Aprobada. La raíz pasó de 2 a 1 y el árbol quedó vacío al eliminar 1. |
| P11 | M3 | Normal y errónea | Agregar segunda facultad y dos carreras. Probar códigos inválidos o duplicados. | Estudiante integrado en su carrera y rechazo de códigos inválidos o repetidos. | Aprobada. Matrícula 72 quedó en C2, C3 quedó vacía y se rechazaron las altas inválidas. |
| P12 | M2 | Normal | Encolar y atender S1, S2, S3 | Atención FIFO y cero pendientes al final. | Aprobada. Se atendieron en el orden 1, 2, 3 y la cola quedó vacía. |
| P13 | M2 | Extrema | Extraer en vacío, encolar un nulo y reinsertar tras vaciar | Extracción vacía y nulo rechazados. La cola admite un nuevo ciclo. | Aprobada. Se verificaron ambos errores y el orden para las nuevas inserciones. |
| P14 | M2 | Normal | Registrar dos cambios de nombre de 80 y revertir ambos | Recuperar primero «Nombre corregido» y luego «Estudiante 80». | Aprobada. Ambos valores se restauraron en orden y quedaron cero cambios pendientes. |
| P15 | M2 | Errónea | Deshacer con pila vacía y enviar un cambio nulo o idéntico | El nombre permanece igual y el historial sigue vacío. | Aprobada. Ana conservó su nombre y no se agregó ninguna entrada. |
| P16 | M2 | Errónea | Encolar con referencias inválidas o reutilizar S1 | Solo entra una S1 válida. La reutilización se rechaza incluso después de atenderla. | Aprobada. La única solicitud válida se atendió y la cola quedó vacía. |
| P17 | M1, M2, M4 | Integración | Borrar a Ana o PC1 con una solicitud pendiente y repetir tras atenderla | Proteger las referencias mientras la solicitud esté pendiente. | Aprobada. Ambas bajas se bloquearon hasta atender S1 y después se ejecutaron. |
| P18 | M1, M2, M5 | Integración | Eliminar y reinscribir 50 con amistades y cambios intercalados | Retirar sus amistades y su historial, preservando el orden LIFO de 70. | Aprobada. 50 se reinscribió sin amigos ni cambios heredados. Se revirtieron los dos cambios de 70 en orden. |
| P19 | M4 | Normal y extrema | BFS de PC1 y de los vértices PC4 y PC2. Verificar los pesos. | BFS oficial, PC4 aislado, dirección respetada y cuatro pesos almacenados. | Aprobada. PC1 alcanzó `[PC1,SW1,PC3,SRV1,PC2]`. PC4 y PC2 solo se alcanzaron a sí mismos desde sus orígenes. |
| P20 | M4 | Normal | DFS desde PC1 en el escenario oficial | `[PC1,SW1,SRV1,PC2,PC3]` y PC4 no alcanzable. | Aprobada. Coincidieron orden y nodos no alcanzables. |
| P21 | M4 | Integración | Retirar SW1 → SRV1 y volver a recorrer | BFS y DFS llegan a `[PC1,SW1,PC3]`. | Aprobada. Ambos detectaron `[SRV1,PC2,PC4]` como no alcanzables. |
| P22 | M4 | Extrema | Agregar ciclos, caminos convergentes y conexión a PC4 | Visitar cada vértice una vez y alcanzar los seis. | Aprobada. Ambas secuencias fueron las esperadas y no quedaron equipos no alcanzables. |
| P23 | M4 | Errónea | Duplicar vértice y arista, usar nulo o peso 0, crear lazo e intentar recorridos o bajas inválidos | Rechazar los datos inválidos y conservar las cuatro conexiones. | Aprobada. La red original conservó sus cuatro conexiones. |
| P24 | M4 | Extrema | Eliminar SW1 con dos aristas entrantes y dos salientes. Registrarlo de nuevo. | Limpiar todas las conexiones del antiguo vértice. | Aprobada. Quedaron PC1 → PC3 y SRV1 → PC2. El nuevo SW1 no heredó aristas. |
| P25 | M5 | Normal | Recomendar a partir de Ana | Diego con 2 amigos en común y Elena con 1, en ese orden. | Aprobada. Se verificaron ambos nombres, conteos y posiciones. |
| P26 | M5 | Extrema | Hacer amigo directo a Diego y agregar un contacto de tercer grado. Consultar también a 80. | Excluir directos, el propio usuario y el tercer grado. Devolver lista vacía si no hay candidatos. | Aprobada. Ana solo obtuvo a Elena y 80 no obtuvo recomendaciones. |
| P27 | M5 | Normal | Quitar amistad Diego–Beto | Retiro recíproco. Diego y Elena empatan y se ordenan por matrícula. | Aprobada. Diego (40) quedó antes de Elena (60), ambos con 1. |
| P28 | M5 | Errónea | Duplicar amistad inversa, agregar lazo y usar referencias inexistentes | Rechazar operaciones sin cambiar las amistades de Ana. | Aprobada. Ana conservó a Beto y Carla como amigos directos. |
| P29 | M6 | Integración | Ejecutar opción 6 dos veces con un estudiante inscrito en la sesión | Resultado oficial idéntico y sesión de trabajo intacta. | Aprobada. Coincidieron las dos salidas y el ABB de la sesión conservó la matrícula 15. |
| P30 | M6 | Integración y errónea | Enviar opción no numérica y fuera de rango, crear, buscar, editar, deshacer y eliminar | Validar entradas y completar las operaciones en una misma sesión. | Aprobada. El menú informó los errores y completó el CRUD, la reversión y el cierre. |
| P31 | M6 | Extrema | Agotar la entrada a la mitad del formulario de alta | Cerrar limpiamente sin alta incompleta. | Aprobada. La sesión se cerró y el vector quedó vacío. |

## Evidencias de ejecución

| Registro | Qué muestra |
|---|---|
| [Caso integrador](evidencias/salida-caso.txt) | Recorridos del ABB, eliminación de la matrícula 30, solicitudes FIFO, cambios LIFO, rutas y recomendaciones. |
| [Suite automatizada](evidencias/salida-pruebas.txt) | Resultado de los 31 casos y resumen de la ejecución. |
| [Sesión del menú](evidencias/salida-menu.txt) | Validación de entradas, errores con cola y pila vacías y ejecución del caso integrador. |

Las [entradas de la sesión](evidencias/entradas-menu.txt) permiten reproducir la prueba del menú.
