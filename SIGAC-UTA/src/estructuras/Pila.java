package estructuras;

import java.util.NoSuchElementException;
import java.util.Objects;

/** Pila LIFO que utiliza el ultimo indice del vector como cima. */
public final class Pila<T> {
    private final Vector<T> elementos = new Vector<>();

    public boolean estaVacia() {
        return elementos.estaVacio();
    }

    public int tamano() {
        return elementos.tamano();
    }

    public void apilar(T valor) {
        elementos.agregar(Objects.requireNonNull(valor, "El valor para la pila es obligatorio."));
    }

    public T cima() {
        validarContenido();
        return elementos.obtener(elementos.tamano() - 1);
    }

    public T desapilar() {
        validarContenido();
        return elementos.eliminar(elementos.tamano() - 1);
    }

    private void validarContenido() {
        if (estaVacia()) {
            throw new NoSuchElementException("La pila esta vacia.");
        }
    }
}