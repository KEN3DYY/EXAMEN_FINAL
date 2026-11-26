package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/** Lista simple que conserva el orden de inserción. */
public final class ListaEnlazada<T> implements Iterable<T> {
    private static final class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
        }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;

    public void agregar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (ultimo == null) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
    }

    public boolean eliminarPrimero(Predicate<T> coincide) {
        Nodo<T> anterior = null;
        for (Nodo<T> actual = primero; actual != null; actual = actual.siguiente) {
            if (coincide.test(actual.valor)) {
                if (anterior == null) {
                    primero = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == ultimo) {
                    ultimo = anterior;
                }
                return true;
            }
            anterior = actual;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = primero;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T valor = actual.valor;
                actual = actual.siguiente;
                return valor;
            }
        };
    }
}
