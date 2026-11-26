# Guía de comprobación 

## ¿Cómo se elimina un nodo del ABB con dos hijos?

En `ArbolBinarioBusqueda.eliminar` se localiza el nodo y su padre. Cuando tiene dos hijos, se busca el mínimo del subárbol derecho. Ese sucesor aporta su clave y su valor al nodo que se está eliminando. Después se retira el sucesor de su ubicación anterior y se conecta su hijo derecho al enlace correspondiente. Para la matrícula 30, el sucesor es 40. El inorden pasa de `[20, 30, 40, 50, 60, 70, 80]` a `[20, 40, 50, 60, 70, 80]`. La prueba P09 comprueba un caso en el que el sucesor sí tiene hijo derecho.

## ¿Cómo determina la cola el orden del BFS?

En `Grafo.bfs` se marca y encola el origen. Al extraer un equipo, se encolan sus vecinos aún sin visitar en el orden de inserción. La cola FIFO permite terminar un nivel antes de procesar el siguiente. Marcar al encolar garantiza que dos caminos que convergen al mismo equipo no lo agreguen dos veces. Desde PC1 se encolan SW1 y PC3. PC3 se visita antes que SRV1 porque ya estaba en la cola.

## ¿Cómo se excluyen los amigos directos?

En `RedSocial.recomendar` se crea un arreglo de marcas y se excluyen tanto la persona consultada como todos sus amigos directos. Al recorrer los vecinos de esos amigos, solo suman los candidatos sin marca. Para Ana se excluyen Ana, Beto y Carla. Diego suma por Beto y Carla. Elena suma por Carla. Las aristas duplicadas se rechazan, así que cada amigo directo puede aportar como máximo un punto a cada candidato.

## ¿Cómo se retiran las aristas entrantes al eliminar un vértice?

En `Grafo.eliminarVertice` se retira el vértice del vector. Con ello quedan fuera sus aristas salientes. Para limpiar las entrantes, se recorren los vértices que permanecen y se retira de cada lista la arista cuyo destino apunta al vértice eliminado. Después se actualizan las posiciones que utilizan BFS y DFS. La prueba P24 agrega dos entradas a SW1 y comprueba que al eliminar ese equipo se retiren tanto las entrantes como las salientes. También lo registra de nuevo para verificar que no conserva conexiones antiguas.
