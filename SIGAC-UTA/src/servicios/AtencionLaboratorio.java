package servicios;

import estructuras.ArbolBinarioBusqueda;
import estructuras.Cola;
import estructuras.Vector;
import modelo.SolicitudLaboratorio;

public final class AtencionLaboratorio {
    private final Cola<SolicitudLaboratorio> pendientes = new Cola<>();
    private final ArbolBinarioBusqueda<Boolean> identificadores = new ArbolBinarioBusqueda<>();

    void registrar(SolicitudLaboratorio solicitud) {
        if (!identificadores.insertar(solicitud.id(), Boolean.TRUE)) {
            throw new IllegalArgumentException("La solicitud S" + solicitud.id() + " ya fue registrada.");
        }
        pendientes.encolar(solicitud);
    }

    public SolicitudLaboratorio atenderSiguiente() {
        return pendientes.desencolar();
    }

    public Vector<SolicitudLaboratorio> listarPendientes() {
        Vector<SolicitudLaboratorio> resultado = new Vector<>();
        for (SolicitudLaboratorio solicitud : pendientes) {
            resultado.agregar(solicitud);
        }
        return resultado;
    }

    boolean tienePendientesDeEstudiante(int matricula) {
        for (SolicitudLaboratorio solicitud : pendientes) {
            if (solicitud.matricula() == matricula) {
                return true;
            }
        }
        return false;
    }

    boolean tienePendientesDeEquipo(String codigo) {
        for (SolicitudLaboratorio solicitud : pendientes) {
            if (solicitud.equipo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }
}
