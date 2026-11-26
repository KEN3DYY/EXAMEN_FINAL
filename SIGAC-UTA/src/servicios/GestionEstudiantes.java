package servicios;

import estructuras.ArbolBinarioBusqueda;
import estructuras.ArbolBinarioBusqueda.Busqueda;
import estructuras.ListaEnlazada;
import estructuras.Vector;
import modelo.Estudiante;

public final class GestionEstudiantes {
    public record Comparacion(Busqueda<Estudiante> secuencial, Busqueda<Estudiante> abb) {}

    private final Vector<Estudiante> estudiantes = new Vector<>();
    private final ListaEnlazada<Estudiante> ordenInsercion = new ListaEnlazada<>();
    private final ArbolBinarioBusqueda<Estudiante> indice = new ArbolBinarioBusqueda<>();
    private final OrganizacionAcademica organizacion;

    GestionEstudiantes(OrganizacionAcademica organizacion) {
        this.organizacion = organizacion;
    }

    Estudiante registrar(int matricula, String nombre, String codigoCarrera) {
        Estudiante estudiante = new Estudiante(matricula, nombre, codigoCarrera);
        if (indice.buscar(matricula).encontrada()) {
            throw new IllegalArgumentException("La matricula " + matricula + " ya esta registrada.");
        }
        organizacion.agregarEstudiante(estudiante);
        estudiantes.agregar(estudiante);
        ordenInsercion.agregar(estudiante);
        indice.insertar(matricula, estudiante);
        return estudiante;
    }

    void eliminar(Estudiante estudiante) {
        organizacion.retirarEstudiante(estudiante);
        for (int i = 0; i < estudiantes.tamano(); i++) {
            if (estudiantes.obtener(i) == estudiante) {
                estudiantes.eliminar(i);
                break;
            }
        }
        ordenInsercion.eliminarPrimero(actual -> actual == estudiante);
        indice.eliminar(estudiante.matricula());
    }

    Estudiante exigir(int matricula) {
        validarMatricula(matricula);
        Estudiante estudiante = indice.buscar(matricula).valor();
        if (estudiante == null) {
            throw new IllegalArgumentException("La matricula " + matricula + " no existe.");
        }
        return estudiante;
    }

    public Vector<Estudiante> listar() {
        return estudiantes.copia();
    }

    public Comparacion compararBusquedas(int matricula) {
        validarMatricula(matricula);
        return new Comparacion(buscarSecuencial(matricula), indice.buscar(matricula));
    }

    public Vector<Integer> preorden() {
        return indice.preorden();
    }

    public Vector<Integer> inorden() {
        return indice.inorden();
    }

    public Vector<Integer> posorden() {
        return indice.posorden();
    }

    private Busqueda<Estudiante> buscarSecuencial(int matricula) {
        int comparaciones = 0;
        for (Estudiante estudiante : ordenInsercion) {
            comparaciones++;
            if (estudiante.matricula() == matricula) {
                return new Busqueda<>(estudiante, comparaciones);
            }
        }
        return new Busqueda<>(null, comparaciones);
    }

    private void validarMatricula(int matricula) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("La matricula debe ser un entero positivo.");
        }
    }
}
