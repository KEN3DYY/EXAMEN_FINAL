package estructuras;

import java.util.Objects;

/** ABB sin balanceo, con claves enteras únicas. */
public final class ArbolBinarioBusqueda<T> {
    public record Busqueda<T>(T valor, int comparaciones) {
        public boolean encontrada() {
            return valor != null;
        }
    }

    private static final class Nodo<T> {
        private int clave;
        private T valor;
        private Nodo<T> izquierdo;
        private Nodo<T> derecho;

        private Nodo(int clave, T valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private Nodo<T> raiz;

    public boolean insertar(int clave, T valor) {
        Nodo<T> nuevo = new Nodo<>(clave, Objects.requireNonNull(valor, "El valor es obligatorio."));
        if (raiz == null) {
            raiz = nuevo;
            return true;
        }
        Nodo<T> actual = raiz;
        while (true) {
            if (clave == actual.clave) {
                return false;
            }
            if (clave < actual.clave) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = nuevo;
                    return true;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = nuevo;
                    return true;
                }
                actual = actual.derecho;
            }
        }
    }

    public Busqueda<T> buscar(int clave) {
        int comparaciones = 0;
        Nodo<T> actual = raiz;
        while (actual != null) {
            comparaciones++;
            if (clave == actual.clave) {
                return new Busqueda<>(actual.valor, comparaciones);
            }
            actual = clave < actual.clave ? actual.izquierdo : actual.derecho;
        }
        return new Busqueda<>(null, comparaciones);
    }

    public boolean eliminar(int clave) {
        Nodo<T> padre = null;
        Nodo<T> actual = raiz;
        while (actual != null && actual.clave != clave) {
            padre = actual;
            actual = clave < actual.clave ? actual.izquierdo : actual.derecho;
        }
        if (actual == null) {
            return false;
        }
        if (actual.izquierdo != null && actual.derecho != null) {
            Nodo<T> padreSucesor = actual;
            Nodo<T> sucesor = actual.derecho;
            while (sucesor.izquierdo != null) {
                padreSucesor = sucesor;
                sucesor = sucesor.izquierdo;
            }
            actual.clave = sucesor.clave;
            actual.valor = sucesor.valor;
            reemplazarEnlace(padreSucesor, sucesor, sucesor.derecho);
        } else {
            Nodo<T> hijo = actual.izquierdo != null ? actual.izquierdo : actual.derecho;
            reemplazarEnlace(padre, actual, hijo);
        }
        return true;
    }

    private void reemplazarEnlace(Nodo<T> padre, Nodo<T> anterior, Nodo<T> nuevo) {
        if (padre == null) {
            raiz = nuevo;
        } else if (padre.izquierdo == anterior) {
            padre.izquierdo = nuevo;
        } else {
            padre.derecho = nuevo;
        }
    }

    public Vector<Integer> preorden() {
        return recorrer(Orden.PREORDEN);
    }

    public Vector<Integer> inorden() {
        return recorrer(Orden.INORDEN);
    }

    public Vector<Integer> posorden() {
        return recorrer(Orden.POSORDEN);
    }

    private enum Orden { PREORDEN, INORDEN, POSORDEN }

    private record Visita<T>(Nodo<T> nodo, boolean emitir) {}

    private Vector<Integer> recorrer(Orden orden) {
        Vector<Integer> resultado = new Vector<>();
        Vector<Visita<T>> pendientes = new Vector<>();
        agregarVisita(pendientes, raiz);
        while (!pendientes.estaVacio()) {
            Visita<T> visita = pendientes.eliminar(pendientes.tamano() - 1);
            Nodo<T> nodo = visita.nodo();
            if (visita.emitir()) {
                resultado.agregar(nodo.clave);
                continue;
            }
            // Se apila en orden inverso al de procesamiento.
            if (orden == Orden.POSORDEN) {
                pendientes.agregar(new Visita<>(nodo, true));
            }
            agregarVisita(pendientes, nodo.derecho);
            if (orden == Orden.INORDEN) {
                pendientes.agregar(new Visita<>(nodo, true));
            }
            agregarVisita(pendientes, nodo.izquierdo);
            if (orden == Orden.PREORDEN) {
                pendientes.agregar(new Visita<>(nodo, true));
            }
        }
        return resultado;
    }

    private void agregarVisita(Vector<Visita<T>> pendientes, Nodo<T> nodo) {
        if (nodo != null) {
            pendientes.agregar(new Visita<>(nodo, false));
        }
    }
}
