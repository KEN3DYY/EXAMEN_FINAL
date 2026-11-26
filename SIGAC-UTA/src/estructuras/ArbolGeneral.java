package estructuras;

import java.util.Objects;

/** Cada nodo puede tener cualquier cantidad de hijos. */
public final class ArbolGeneral<T> {
    public static final class Nodo<T> {
        private final T valor;
        private final ArbolGeneral<T> arbol;
        private final Nodo<T> padre;
        private final Vector<Nodo<T>> hijos = new Vector<>();
        private boolean activo = true;

        private Nodo(T valor, ArbolGeneral<T> arbol, Nodo<T> padre) {
            this.valor = Objects.requireNonNull(valor, "El valor del nodo es obligatorio.");
            this.arbol = arbol;
            this.padre = padre;
        }

        public T valor() {
            return valor;
        }

        public Iterable<Nodo<T>> hijos() {
            return hijos::iterator;
        }
    }

    private final Nodo<T> raiz;

    public ArbolGeneral(T valorRaiz) {
        raiz = new Nodo<>(valorRaiz, this, null);
    }

    public Nodo<T> raiz() {
        return raiz;
    }

    public Nodo<T> agregarHijo(Nodo<T> padre, T valor) {
        validarNodo(padre);
        Nodo<T> hijo = new Nodo<>(valor, this, padre);
        padre.hijos.agregar(hijo);
        return hijo;
    }

    public void eliminarHoja(Nodo<T> hoja) {
        validarNodo(hoja);
        if (hoja.padre == null || !hoja.hijos.estaVacio()) {
            throw new IllegalArgumentException("Solo se puede retirar una hoja distinta de la raiz.");
        }
        Vector<Nodo<T>> hermanos = hoja.padre.hijos;
        for (int i = 0; i < hermanos.tamano(); i++) {
            if (hermanos.obtener(i) == hoja) {
                hermanos.eliminar(i);
                hoja.activo = false;
                return;
            }
        }
        throw new IllegalStateException("El nodo activo debe pertenecer a su padre.");
    }

    private void validarNodo(Nodo<T> nodo) {
        if (nodo == null || nodo.arbol != this || !nodo.activo) {
            throw new IllegalArgumentException("El nodo debe estar activo en este arbol.");
        }
    }
}
