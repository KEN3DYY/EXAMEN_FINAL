package estructuras;

import java.util.Objects;

/** Grafo dinámico con listas de adyacencia en el orden de inserción. */
public final class Grafo<K> {
    public record Conexion<K>(K origen, K destino, int peso) {
        @Override
        public String toString() {
            return origen + " -> " + destino + " (peso " + peso + ")";
        }
    }

    public record Recorrido<K>(Vector<K> visitados, Vector<K> noAlcanzables) {}

    private static final class Vertice<K> {
        private final K id;
        private final Vector<Arista<K>> salientes = new Vector<>();
        private int posicion;

        private Vertice(K id, int posicion) {
            this.id = id;
            this.posicion = posicion;
        }
    }

    private record Arista<K>(Vertice<K> destino, int peso) {}

    private static final class Marco<K> {
        private final Vertice<K> vertice;
        private int siguienteVecino;

        private Marco(Vertice<K> vertice) {
            this.vertice = vertice;
        }
    }

    private final boolean dirigido;
    private final Vector<Vertice<K>> vertices = new Vector<>();

    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
    }

    public void agregarVertice(K id) {
        validarId(id);
        if (buscarVertice(id) != null) {
            throw new IllegalArgumentException("Ya existe el vértice " + id + ".");
        }
        vertices.agregar(new Vertice<>(id, vertices.tamano()));
    }

    public void eliminarVertice(K id) {
        Vertice<K> eliminado = exigirVertice(id);
        vertices.eliminar(eliminado.posicion);
        for (int i = 0; i < vertices.tamano(); i++) {
            Vertice<K> actual = vertices.obtener(i);
            retirarArista(actual, eliminado);
            actual.posicion = i;
        }
    }

    public void conectar(K idOrigen, K idDestino, int peso) {
        Vertice<K> origen = exigirVertice(idOrigen);
        Vertice<K> destino = exigirVertice(idDestino);
        if (origen == destino) {
            throw new IllegalArgumentException("Una conexión requiere dos vértices distintos.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser un entero positivo.");
        }
        if (indiceArista(origen, destino) >= 0) {
            throw new IllegalArgumentException("Esa conexión ya existe.");
        }
        origen.salientes.agregar(new Arista<>(destino, peso));
        if (!dirigido) {
            destino.salientes.agregar(new Arista<>(origen, peso));
        }
    }

    public void desconectar(K idOrigen, K idDestino) {
        Vertice<K> origen = exigirVertice(idOrigen);
        Vertice<K> destino = exigirVertice(idDestino);
        if (!retirarArista(origen, destino)) {
            throw new IllegalArgumentException("La conexión indicada no existe.");
        }
        if (!dirigido) {
            retirarArista(destino, origen);
        }
    }

    public K exigirId(K id) {
        return exigirVertice(id).id;
    }

    public int cantidadVertices() {
        return vertices.tamano();
    }

    public int posicionDe(K id) {
        return exigirVertice(id).posicion;
    }

    public K identificadorEn(int posicion) {
        return vertices.obtener(posicion).id;
    }

    public Vector<Integer> posicionesVecinas(int posicion) {
        Vector<Integer> vecinos = new Vector<>();
        for (Arista<K> arista : vertices.obtener(posicion).salientes) {
            vecinos.agregar(arista.destino().posicion);
        }
        return vecinos;
    }

    public Vector<K> listarVertices() {
        Vector<K> identificadores = new Vector<>();
        for (Vertice<K> vertice : vertices) {
            identificadores.agregar(vertice.id);
        }
        return identificadores;
    }

    public Vector<Conexion<K>> listarConexiones() {
        Vector<Conexion<K>> conexiones = new Vector<>();
        for (Vertice<K> origen : vertices) {
            for (Arista<K> arista : origen.salientes) {
                Vertice<K> destino = arista.destino();
                if (dirigido || origen.posicion < destino.posicion) {
                    conexiones.agregar(new Conexion<>(origen.id, destino.id, arista.peso()));
                }
            }
        }
        return conexiones;
    }

    public Recorrido<K> bfs(K idOrigen) {
        Vertice<K> origen = exigirVertice(idOrigen);
        boolean[] visitado = new boolean[vertices.tamano()];
        Vector<K> orden = new Vector<>();
        Cola<Vertice<K>> pendientes = new Cola<>();
        visitado[origen.posicion] = true;
        pendientes.encolar(origen);
        while (!pendientes.estaVacia()) {
            Vertice<K> actual = pendientes.desencolar();
            orden.agregar(actual.id);
            for (Arista<K> arista : actual.salientes) {
                Vertice<K> vecino = arista.destino();
                if (!visitado[vecino.posicion]) {
                    visitado[vecino.posicion] = true;
                    pendientes.encolar(vecino);
                }
            }
        }
        return completarRecorrido(orden, visitado);
    }

    public Recorrido<K> dfs(K idOrigen) {
        Vertice<K> origen = exigirVertice(idOrigen);
        boolean[] visitado = new boolean[vertices.tamano()];
        Vector<K> orden = new Vector<>();
        Pila<Marco<K>> camino = new Pila<>();
        visitar(origen, visitado, orden, camino);
        while (!camino.estaVacia()) {
            Marco<K> marco = camino.cima();
            if (marco.siguienteVecino == marco.vertice.salientes.tamano()) {
                camino.desapilar();
                continue;
            }
            Vertice<K> vecino = marco.vertice.salientes.obtener(marco.siguienteVecino++).destino();
            if (!visitado[vecino.posicion]) {
                visitar(vecino, visitado, orden, camino);
            }
        }
        return completarRecorrido(orden, visitado);
    }

    private void visitar(Vertice<K> vertice, boolean[] visitado, Vector<K> orden, Pila<Marco<K>> camino) {
        visitado[vertice.posicion] = true;
        orden.agregar(vertice.id);
        camino.apilar(new Marco<>(vertice));
    }

    private Recorrido<K> completarRecorrido(Vector<K> orden, boolean[] visitado) {
        Vector<K> noAlcanzables = new Vector<>();
        for (Vertice<K> vertice : vertices) {
            if (!visitado[vertice.posicion]) {
                noAlcanzables.agregar(vertice.id);
            }
        }
        return new Recorrido<>(orden, noAlcanzables);
    }

    private Vertice<K> exigirVertice(K id) {
        validarId(id);
        Vertice<K> vertice = buscarVertice(id);
        if (vertice == null) {
            throw new IllegalArgumentException("El vértice " + id + " no existe.");
        }
        return vertice;
    }

    private Vertice<K> buscarVertice(K id) {
        for (Vertice<K> vertice : vertices) {
            if (Objects.equals(vertice.id, id)) {
                return vertice;
            }
        }
        return null;
    }

    private void validarId(K id) {
        if (id == null) {
            throw new IllegalArgumentException("El identificador del vértice es obligatorio.");
        }
    }

    private boolean retirarArista(Vertice<K> origen, Vertice<K> destino) {
        int indice = indiceArista(origen, destino);
        if (indice < 0) {
            return false;
        }
        origen.salientes.eliminar(indice);
        return true;
    }

    private int indiceArista(Vertice<K> origen, Vertice<K> destino) {
        for (int i = 0; i < origen.salientes.tamano(); i++) {
            if (origen.salientes.obtener(i).destino() == destino) {
                return i;
            }
        }
        return -1;
    }
}
