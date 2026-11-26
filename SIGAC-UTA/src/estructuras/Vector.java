package estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

/** Arreglo redimensionable con acceso directo por índice. */
public final class Vector<T> implements Iterable<T> {
    private Object[] datos = new Object[4];
    private int tamano;

    public int tamano() {
        return tamano;
    }

    public boolean estaVacio() {
        return tamano == 0;
    }

    public void agregar(T valor) {
        if (tamano == datos.length) {
            Object[] ampliado = new Object[datos.length * 2];
            System.arraycopy(datos, 0, ampliado, 0, tamano);
            datos = ampliado;
        }
        datos[tamano++] = valor;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        validarIndice(indice);
        return (T) datos[indice];
    }

    public void reemplazar(int indice, T valor) {
        validarIndice(indice);
        datos[indice] = valor;
    }

    public T eliminar(int indice) {
        T valor = obtener(indice);
        System.arraycopy(datos, indice + 1, datos, indice, tamano - indice - 1);
        datos[--tamano] = null;
        return valor;
    }

    public Vector<T> copia() {
        Vector<T> resultado = new Vector<>();
        for (T valor : this) {
            resultado.agregar(valor);
        }
        return resultado;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera del vector: " + indice);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int indice;

            @Override
            public boolean hasNext() {
                return indice < tamano;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return obtener(indice++);
            }
        };
    }

    @Override
    public String toString() {
        StringJoiner texto = new StringJoiner(", ", "[", "]");
        for (T valor : this) {
            texto.add(String.valueOf(valor));
        }
        return texto.toString();
    }
}
