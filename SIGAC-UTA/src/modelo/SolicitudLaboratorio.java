package modelo;

public record SolicitudLaboratorio(int id, int matricula, String equipo) {
    public SolicitudLaboratorio {
        if (id <= 0 || matricula <= 0) {
            throw new IllegalArgumentException("El ID de la solicitud y la matrícula deben ser positivos.");
        }
        equipo = Estudiante.textoObligatorio(equipo, "El equipo");
    }

    @Override
    public String toString() {
        return "S" + id + " | matrícula " + matricula + " | equipo " + equipo;
    }
}
