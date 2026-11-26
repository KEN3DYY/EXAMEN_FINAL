package app;

import estructuras.ArbolBinarioBusqueda.Busqueda;
import estructuras.Grafo.Recorrido;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.IntConsumer;
import modelo.Estudiante;
import servicios.Sigac;

public final class Menu {
    private static final String PRINCIPAL = """
            === SIGAC-UTA ===
            1. Estudiantes
            2. Atencion de laboratorio e historial
            3. Organizacion academica y ABB
            4. Red de laboratorios
            5. Amistades y recomendaciones
            6. Ejecutar caso integrador
            0. Salir
            """;

    private static final String ESTUDIANTES = """
            -- Estudiantes --
            1. Registrar estudiante
            2. Listar estudiantes
            3. Buscar por matricula y comparar
            4. Actualizar nombre
            5. Eliminar estudiante
            0. Volver
            """;

    private static final String ATENCION = """
            -- Atencion de laboratorio e historial --
            1. Registrar solicitud
            2. Listar solicitudes pendientes
            3. Atender siguiente solicitud
            4. Deshacer ultimo cambio de nombre
            0. Volver
            """;

    private static final String ACADEMICO = """
            -- Organizacion academica y ABB --
            1. Mostrar jerarquia academica
            2. Registrar facultad
            3. Registrar carrera
            4. Mostrar recorridos del ABB
            0. Volver
            """;

    private static final String LABORATORIO = """
            -- Red de laboratorios --
            1. Registrar equipo
            2. Listar equipos y conexiones
            3. Agregar conexion
            4. Eliminar conexion
            5. Ejecutar BFS y mostrar no alcanzables
            6. Ejecutar DFS y mostrar no alcanzables
            7. Eliminar equipo
            0. Volver
            """;

    private static final String SOCIAL = """
            -- Amistades y recomendaciones --
            1. Registrar amistad
            2. Quitar amistad
            3. Listar amigos directos
            4. Recomendar colaboradores
            0. Volver
            """;

    private static final class FinEntrada extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private final Sigac sistema;
    private final Scanner entrada;
    private final PrintStream salida;

    public Menu(Sigac sistema, Scanner entrada, PrintStream salida) {
        this.sistema = sistema;
        this.entrada = entrada;
        this.salida = salida;
    }

    public void ejecutar() {
        try {
            abrir(PRINCIPAL, opcion -> {
                switch (opcion) {
                    case 1 -> abrir(ESTUDIANTES, this::estudiantes);
                    case 2 -> abrir(ATENCION, this::atencion);
                    case 3 -> abrir(ACADEMICO, this::academico);
                    case 4 -> abrir(LABORATORIO, this::laboratorio);
                    case 5 -> abrir(SOCIAL, this::social);
                    case 6 -> CasoIntegrador.ejecutar(salida);
                    default -> opcionInvalida();
                }
            });
        } catch (FinEntrada fin) {
            salida.println();
        }
        salida.println("Fin de sesion.");
    }

    private void abrir(String opciones, IntConsumer procesar) {
        while (true) {
            salida.println();
            salida.print(opciones);
            try {
                int opcion = entero("Opcion: ");
                if (opcion == 0) {
                    return;
                }
                procesar.accept(opcion);
            } catch (IllegalArgumentException | NoSuchElementException error) {
                salida.println("Error: " + error.getMessage());
            }
        }
    }

    private void estudiantes(int opcion) {
        switch (opcion) {
            case 1 -> {
                Estudiante estudiante = sistema.registrarEstudiante(entero("Matricula: "),
                        leer("Nombre: "), leer("Codigo de carrera (p. ej. SOF): "));
                salida.println("Estudiante registrado: " + estudiante);
            }
            case 2 -> listar(sistema.estudiantes().listar(), "Sin estudiantes registrados.");
            case 3 -> {
                var comparacion = sistema.estudiantes().compararBusquedas(entero("Matricula: "));
                imprimirBusqueda("Secuencial", comparacion.secuencial());
                imprimirBusqueda("ABB", comparacion.abb());
            }
            case 4 -> {
                boolean cambio = sistema.actualizarNombre(entero("Matricula: "), leer("Nuevo nombre: "));
                salida.println(cambio ? "Nombre actualizado." : "El nombre ya tiene ese valor.");
            }
            case 5 -> salida.println("Estudiante eliminado: "
                    + sistema.eliminarEstudiante(entero("Matricula: ")));
            default -> opcionInvalida();
        }
    }

    private void atencion(int opcion) {
        switch (opcion) {
            case 1 -> {
                sistema.registrarSolicitud(entero("ID numerico de solicitud: "), entero("Matricula: "),
                        leer("Codigo de equipo: "));
                salida.println("Solicitud encolada.");
            }
            case 2 -> listar(sistema.atencion().listarPendientes(), "Sin solicitudes pendientes.");
            case 3 -> salida.println("Atendida: " + sistema.atencion().atenderSiguiente());
            case 4 -> salida.println("Nombre restaurado: " + sistema.deshacerCambio());
            default -> opcionInvalida();
        }
    }

    private void academico(int opcion) {
        switch (opcion) {
            case 1 -> listar(sistema.organizacion().lineasJerarquia(), "Jerarquia vacia.");
            case 2 -> {
                sistema.organizacion().agregarFacultad(leer("Codigo de facultad: "), leer("Nombre: "));
                salida.println("Facultad registrada.");
            }
            case 3 -> {
                sistema.organizacion().agregarCarrera(leer("Codigo de facultad: "),
                        leer("Codigo de carrera: "), leer("Nombre: "));
                salida.println("Carrera registrada.");
            }
            case 4 -> {
                salida.println("Preorden: " + sistema.estudiantes().preorden());
                salida.println("Inorden:  " + sistema.estudiantes().inorden());
                salida.println("Posorden: " + sistema.estudiantes().posorden());
            }
            default -> opcionInvalida();
        }
    }

    private void laboratorio(int opcion) {
        switch (opcion) {
            case 1 -> {
                sistema.laboratorio().registrarEquipo(leer("Codigo de equipo: "));
                salida.println("Equipo registrado.");
            }
            case 2 -> {
                salida.println("Equipos: " + sistema.laboratorio().listarEquipos());
                listar(sistema.laboratorio().listarConexiones(), "Sin conexiones registradas.");
            }
            case 3 -> {
                sistema.laboratorio().conectar(leer("Equipo origen: "), leer("Equipo destino: "),
                        entero("Peso entero: "));
                salida.println("Conexion registrada.");
            }
            case 4 -> {
                sistema.laboratorio().desconectar(leer("Equipo origen: "), leer("Equipo destino: "));
                salida.println("Conexion eliminada.");
            }
            case 5 -> imprimirRecorrido("BFS", sistema.laboratorio().bfs(leer("Equipo inicial: ")));
            case 6 -> imprimirRecorrido("DFS", sistema.laboratorio().dfs(leer("Equipo inicial: ")));
            case 7 -> {
                sistema.eliminarEquipo(leer("Codigo de equipo: "));
                salida.println("Equipo eliminado.");
            }
            default -> opcionInvalida();
        }
    }

    private void social(int opcion) {
        switch (opcion) {
            case 1 -> {
                sistema.social().agregarAmistad(entero("Primera matricula: "), entero("Segunda matricula: "));
                salida.println("Amistad registrada.");
            }
            case 2 -> {
                sistema.social().quitarAmistad(entero("Primera matricula: "), entero("Segunda matricula: "));
                salida.println("Amistad eliminada.");
            }
            case 3 -> listar(sistema.social().amigosDe(entero("Matricula: ")), "Sin amigos directos.");
            case 4 -> {
                var recomendaciones = sistema.social().recomendar(entero("Matricula: "));
                if (recomendaciones.estaVacio()) {
                    salida.println("Sin contactos de segundo grado para recomendar.");
                }
                for (var recomendacion : recomendaciones) {
                    salida.printf("%s | amigos en comun: %d%n",
                            recomendacion.estudiante(), recomendacion.amigosEnComun());
                }
            }
            default -> opcionInvalida();
        }
    }

    private void imprimirBusqueda(String tipo, Busqueda<Estudiante> resultado) {
        String unidad = resultado.comparaciones() == 1 ? "comparacion" : "comparaciones";
        salida.printf("%s (%d %s): %s%n", tipo, resultado.comparaciones(), unidad,
                resultado.encontrada() ? resultado.valor() : "matricula no encontrada.");
    }

    private void imprimirRecorrido(String algoritmo, Recorrido<String> resultado) {
        salida.println(algoritmo + ": " + resultado.visitados());
        salida.println("No alcanzables: " + resultado.noAlcanzables());
    }

    private void listar(Iterable<?> elementos, String mensajeVacio) {
        if (!elementos.iterator().hasNext()) {
            salida.println(mensajeVacio);
            return;
        }
        for (Object elemento : elementos) {
            salida.println("  " + elemento);
        }
    }

    private int entero(String pregunta) {
        try {
            return Integer.parseInt(leer(pregunta));
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Ingrese un numero entero valido.");
        }
    }

    private String leer(String pregunta) {
        salida.print(pregunta);
        salida.flush();
        if (!entrada.hasNextLine()) {
            throw new FinEntrada();
        }
        return entrada.nextLine().strip();
    }

    private void opcionInvalida() {
        throw new IllegalArgumentException("Opcion fuera del menu.");
    }
}
