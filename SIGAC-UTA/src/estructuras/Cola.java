package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Cola FIFO enlazada con insercion y extraccion en tiempo constante. */
public final class Cola<T> implements Iterable<T> {
    private static final class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
        }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamano;

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return primero == null;
    }

    public void encolar(T valor) {
        Nodo<T> nuevo = new Nodo<>(Objects.requireNonNull(valor, "El valor para la cola es obligatorio."));
        if (ultimo == null) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        tamano++;
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia.");
        }
        T valor = primero.valor;
        primero = primero.siguiente;
        if (primero == null) {
            ultimo = null;
        }
        tamano--;
        return valor;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> siguiente = primero;

            @Override
            public boolean hasNext() {
                return siguiente != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T valor = siguiente.valor;
                siguiente = siguiente.siguiente;
                return valor;
            }
        };
    }
}
