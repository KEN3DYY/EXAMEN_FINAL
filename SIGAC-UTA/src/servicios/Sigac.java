package servicios;

import modelo.Estudiante;
import modelo.SolicitudLaboratorio;

/** Coordina las operaciones que afectan a más de un módulo. */
public final class Sigac {
    private final OrganizacionAcademica organizacion = new OrganizacionAcademica();
    private final GestionEstudiantes estudiantes = new GestionEstudiantes(organizacion);
    private final AtencionLaboratorio atencion = new AtencionLaboratorio();
    private final HistorialCambios historial = new HistorialCambios();
    private final RedLaboratorio laboratorio = new RedLaboratorio();
    private final RedSocial social = new RedSocial(estudiantes);

    public OrganizacionAcademica organizacion() {
        return organizacion;
    }

    public GestionEstudiantes estudiantes() {
        return estudiantes;
    }

    public AtencionLaboratorio atencion() {
        return atencion;
    }

    public RedLaboratorio laboratorio() {
        return laboratorio;
    }

    public RedSocial social() {
        return social;
    }

    public Estudiante registrarEstudiante(int matricula, String nombre, String carrera) {
        Estudiante estudiante = estudiantes.registrar(matricula, nombre, carrera);
        social.registrar(matricula);
        return estudiante;
    }

    public boolean actualizarNombre(int matricula, String nuevoNombre) {
        return historial.cambiarNombre(estudiantes.exigir(matricula), nuevoNombre);
    }

    public Estudiante deshacerCambio() {
        return historial.deshacer();
    }

    public int cambiosPendientes() {
        return historial.cantidad();
    }

    public Estudiante eliminarEstudiante(int matricula) {
        Estudiante estudiante = estudiantes.exigir(matricula);
        if (atencion.tienePendientesDeEstudiante(matricula)) {
            throw new IllegalArgumentException("El estudiante tiene solicitudes de laboratorio pendientes.");
        }
        estudiantes.eliminar(estudiante);
        social.retirar(matricula);
        historial.descartarDe(estudiante);
        return estudiante;
    }

    public void registrarSolicitud(int id, int matricula, String equipo) {
        estudiantes.exigir(matricula);
        String codigo = laboratorio.exigirEquipo(equipo);
        atencion.registrar(new SolicitudLaboratorio(id, matricula, codigo));
    }

    public void eliminarEquipo(String codigo) {
        String equipo = laboratorio.exigirEquipo(codigo);
        if (atencion.tienePendientesDeEquipo(equipo)) {
            throw new IllegalArgumentException("El equipo tiene solicitudes de laboratorio pendientes.");
        }
        laboratorio.retirarEquipo(equipo);
    }
}
