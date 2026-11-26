package modelo;

public final class Estudiante {
    private final int matricula;
    private final String codigoCarrera;
    private String nombre;

    public Estudiante(int matricula, String nombre, String codigoCarrera) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("La matrícula debe ser un entero positivo.");
        }
        this.matricula = matricula;
        this.codigoCarrera = textoObligatorio(codigoCarrera, "El código de carrera");
        cambiarNombre(nombre);
    }

    public int matricula() {
        return matricula;
    }

    public String nombre() {
        return nombre;
    }

    public String codigoCarrera() {
        return codigoCarrera;
    }

    public void cambiarNombre(String nuevoNombre) {
        nombre = textoObligatorio(nuevoNombre, "El nombre");
    }

    public static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor.strip();
    }

    @Override
    public String toString() {
        return matricula + " | " + nombre + " | " + codigoCarrera;
    }
}
