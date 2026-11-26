package servicios;

import estructuras.Pila;
import modelo.Estudiante;

/** Registra cambios efectivos de nombre y guarda el estado anterior para revertirlos. */
final class HistorialCambios {
    private record Cambio(Estudiante estudiante, String anterior) {}

    private final Pila<Cambio> cambios = new Pila<>();

    boolean cambiarNombre(Estudiante estudiante, String nuevoNombre) {
        String nombre = Estudiante.textoObligatorio(nuevoNombre, "El nombre");
        String anterior = estudiante.nombre();
        if (nombre.equals(anterior)) {
            return false;
        }
        estudiante.cambiarNombre(nombre);
        cambios.apilar(new Cambio(estudiante, anterior));
        return true;
    }

    Estudiante deshacer() {
        Cambio cambio = cambios.cima();
        cambio.estudiante().cambiarNombre(cambio.anterior());
        cambios.desapilar();
        return cambio.estudiante();
    }

    int cantidad() {
        return cambios.tamano();
    }

    void descartarDe(Estudiante estudiante) {
        Pila<Cambio> conservados = new Pila<>();
        while (!cambios.estaVacia()) {
            Cambio cambio = cambios.desapilar();
            if (cambio.estudiante() != estudiante) {
                conservados.apilar(cambio);
            }
        }
        while (!conservados.estaVacia()) {
            cambios.apilar(conservados.desapilar());
        }
    }
}
