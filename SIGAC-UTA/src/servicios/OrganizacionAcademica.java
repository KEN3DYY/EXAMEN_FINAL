package servicios;

import estructuras.ArbolGeneral;
import estructuras.ArbolGeneral.Nodo;
import estructuras.Vector;
import modelo.Estudiante;

public final class OrganizacionAcademica {
    public static final String CARRERA_BASE = "SOF";

    private enum Nivel { UNIVERSIDAD, FACULTAD, CARRERA, ESTUDIANTE }

    private record Elemento(Nivel nivel, String codigo, String nombre, Estudiante estudiante) {
        @Override
        public String toString() {
            if (nivel == Nivel.ESTUDIANTE) {
                return "Estudiante " + estudiante.matricula() + " - " + estudiante.nombre();
            }
            return codigo + " - " + nombre;
        }
    }

    private final ArbolGeneral<Elemento> arbol = new ArbolGeneral<>(
            new Elemento(Nivel.UNIVERSIDAD, "UTA", "Universidad Tecnica de Ambato", null));

    public OrganizacionAcademica() {
        agregarFacultad("FISEI", "Facultad de Ingenieria en Sistemas, Electronica e Industrial");
        agregarCarrera("FISEI", CARRERA_BASE, "Ingenieria en Software");
    }

    public void agregarFacultad(String codigo, String nombre) {
        String clave = Estudiante.textoObligatorio(codigo, "El codigo de facultad");
        if (buscarHijo(arbol.raiz(), clave) != null) {
            throw new IllegalArgumentException("Ya existe una facultad con codigo " + clave + ".");
        }
        arbol.agregarHijo(arbol.raiz(), new Elemento(Nivel.FACULTAD, clave,
                Estudiante.textoObligatorio(nombre, "El nombre de la facultad"), null));
    }

    public void agregarCarrera(String codigoFacultad, String codigo, String nombre) {
        Nodo<Elemento> facultad = buscarHijo(arbol.raiz(),
                Estudiante.textoObligatorio(codigoFacultad, "El codigo de facultad"));
        if (facultad == null) {
            throw new IllegalArgumentException("La facultad indicada no existe.");
        }
        String clave = Estudiante.textoObligatorio(codigo, "El codigo de carrera");
        if (buscarCarrera(clave) != null) {
            throw new IllegalArgumentException("Ya existe una carrera con codigo " + clave + ".");
        }
        arbol.agregarHijo(facultad, new Elemento(Nivel.CARRERA, clave,
                Estudiante.textoObligatorio(nombre, "El nombre de la carrera"), null));
    }

    void agregarEstudiante(Estudiante estudiante) {
        Nodo<Elemento> carrera = exigirCarrera(estudiante.codigoCarrera());
        arbol.agregarHijo(carrera, new Elemento(Nivel.ESTUDIANTE,
                Integer.toString(estudiante.matricula()), null, estudiante));
    }

    void retirarEstudiante(Estudiante estudiante) {
        arbol.eliminarHoja(buscarHijo(exigirCarrera(estudiante.codigoCarrera()),
                Integer.toString(estudiante.matricula())));
    }

    public Vector<Integer> matriculasDeCarrera(String codigo) {
        Vector<Integer> matriculas = new Vector<>();
        for (Nodo<Elemento> nodo : exigirCarrera(codigo).hijos()) {
            matriculas.agregar(nodo.valor().estudiante().matricula());
        }
        return matriculas;
    }

    public Vector<String> lineasJerarquia() {
        Vector<String> lineas = new Vector<>();
        escribir(arbol.raiz(), 0, lineas);
        return lineas;
    }

    private void escribir(Nodo<Elemento> nodo, int profundidad, Vector<String> lineas) {
        lineas.agregar("  ".repeat(profundidad) + nodo.valor());
        for (Nodo<Elemento> hijo : nodo.hijos()) {
            escribir(hijo, profundidad + 1, lineas);
        }
    }

    private Nodo<Elemento> exigirCarrera(String codigo) {
        String clave = Estudiante.textoObligatorio(codigo, "El codigo de carrera");
        Nodo<Elemento> carrera = buscarCarrera(clave);
        if (carrera == null) {
            throw new IllegalArgumentException("La carrera " + clave + " no existe.");
        }
        return carrera;
    }

    private Nodo<Elemento> buscarCarrera(String codigo) {
        for (Nodo<Elemento> facultad : arbol.raiz().hijos()) {
            Nodo<Elemento> carrera = buscarHijo(facultad, codigo);
            if (carrera != null) {
                return carrera;
            }
        }
        return null;
    }

    private Nodo<Elemento> buscarHijo(Nodo<Elemento> padre, String codigo) {
        for (Nodo<Elemento> hijo : padre.hijos()) {
            if (hijo.valor().codigo().equals(codigo)) {
                return hijo;
            }
        }
        return null;
    }
}
