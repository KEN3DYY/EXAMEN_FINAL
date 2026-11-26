package app;

import estructuras.Grafo.Recorrido;
import java.io.PrintStream;
import servicios.OrganizacionAcademica;
import servicios.RedSocial.Recomendacion;
import servicios.Sigac;

public final class CasoIntegrador {
    private CasoIntegrador() {}

    public static Sigac preparar() {
        Sigac sistema = new Sigac();
        String carrera = OrganizacionAcademica.CARRERA_BASE;
        sistema.registrarEstudiante(50, "Ana", carrera);
        sistema.registrarEstudiante(30, "Estudiante 30", carrera);
        sistema.registrarEstudiante(70, "Beto", carrera);
        sistema.registrarEstudiante(20, "Carla", carrera);
        sistema.registrarEstudiante(40, "Diego", carrera);
        sistema.registrarEstudiante(60, "Elena", carrera);
        sistema.registrarEstudiante(80, "Estudiante 80", carrera);

        for (String codigo : new String[]{"PC1", "SW1", "SRV1", "PC2", "PC3", "PC4"}) {
            sistema.laboratorio().registrarEquipo(codigo);
        }
        sistema.laboratorio().conectar("PC1", "SW1", 2);
        sistema.laboratorio().conectar("SW1", "SRV1", 3);
        sistema.laboratorio().conectar("SRV1", "PC2", 2);
        sistema.laboratorio().conectar("PC1", "PC3", 1);

        sistema.social().agregarAmistad(50, 70);
        sistema.social().agregarAmistad(50, 20);
        sistema.social().agregarAmistad(70, 40);
        sistema.social().agregarAmistad(20, 40);
        sistema.social().agregarAmistad(20, 60);
        return sistema;
    }

    public static void ejecutar(PrintStream salida) {
        Sigac sistema = preparar();
        salida.println("=== SIGAC-UTA | Caso integrador obligatorio ===");
        salida.println();
        salida.println("[M1 y M3] Estudiantes, comparación de búsquedas y árbol ABB");
        salida.println("Matrículas insertadas: [50, 30, 70, 20, 40, 60, 80]");
        salida.println("Preorden: " + sistema.estudiantes().preorden());
        salida.println("Inorden:  " + sistema.estudiantes().inorden());
        salida.println("Posorden: " + sistema.estudiantes().posorden());
        var comparacion = sistema.estudiantes().compararBusquedas(40);
        salida.printf("Búsqueda de 40: %d comparaciones secuenciales y %d en el ABB.%n",
                comparacion.secuencial().comparaciones(), comparacion.abb().comparaciones());
        sistema.eliminarEstudiante(30);
        salida.println("Inorden después de eliminar 30: " + sistema.estudiantes().inorden());
        comparacion = sistema.estudiantes().compararBusquedas(30);
        salida.printf("Búsqueda de 30: secuencial %s, ABB %s.%n",
                comparacion.secuencial().encontrada() ? "encontrada" : "sin resultado",
                comparacion.abb().encontrada() ? "encontrada" : "sin resultado");
        salida.println("Jerarquía actual:");
        for (String linea : sistema.organizacion().lineasJerarquia()) {
            salida.println("  " + linea);
        }

        salida.println();
        salida.println("[M2] Atención FIFO e historial LIFO");
        sistema.registrarSolicitud(1, 50, "PC1");
        sistema.registrarSolicitud(2, 70, "PC2");
        sistema.registrarSolicitud(3, 20, "PC3");
        salida.println("Solicitudes ingresadas: S1, S2, S3.");
        for (int i = 0; i < 3; i++) {
            salida.println("Atendida: " + sistema.atencion().atenderSiguiente());
        }
        salida.println("Solicitudes pendientes: " + sistema.atencion().listarPendientes().tamano());
        salida.println("Nombre inicial de 80: Estudiante 80.");
        sistema.actualizarNombre(80, "Nombre corregido");
        sistema.actualizarNombre(80, "Nombre confirmado");
        salida.println("Cambios registrados: Nombre corregido, luego Nombre confirmado.");
        salida.println("Primer deshacer: 80 vuelve a " + sistema.deshacerCambio().nombre() + ".");
        salida.println("Segundo deshacer: 80 vuelve a " + sistema.deshacerCambio().nombre() + ".");
        salida.println("Cambios reversibles pendientes: " + sistema.cambiosPendientes());

        salida.println();
        salida.println("[M4] Red dirigida de laboratorio");
        salida.println("Equipos: " + sistema.laboratorio().listarEquipos());
        for (var conexion : sistema.laboratorio().listarConexiones()) {
            salida.println("  " + conexion);
        }
        Recorrido<String> bfs = sistema.laboratorio().bfs("PC1");
        salida.println("BFS desde PC1: " + bfs.visitados());
        salida.println("No alcanzables desde PC1: " + bfs.noAlcanzables());
        salida.println("DFS desde PC1: " + sistema.laboratorio().dfs("PC1").visitados());

        salida.println();
        salida.println("[M5] Recomendaciones para Ana");
        for (Recomendacion recomendacion : sistema.social().recomendar(50)) {
            salida.printf("%s | amigos en común: %d%n",
                    recomendacion.estudiante().nombre(), recomendacion.amigosEnComun());
        }

        salida.println();
        salida.println("[M4] Conectividad actualizada");
        sistema.laboratorio().desconectar("SW1", "SRV1");
        salida.println("Conexión retirada: SW1 -> SRV1.");
        bfs = sistema.laboratorio().bfs("PC1");
        salida.println("BFS desde PC1: " + bfs.visitados());
        salida.println("No alcanzables desde PC1: " + bfs.noAlcanzables());
        salida.println("=== Fin del caso integrador ===");
    }
}
