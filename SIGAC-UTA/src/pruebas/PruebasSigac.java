package pruebas;

import app.CasoIntegrador;
import app.Menu;
import estructuras.ArbolBinarioBusqueda;
import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.Vector;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;
import modelo.Estudiante;
import servicios.OrganizacionAcademica;
import servicios.Sigac;

public final class PruebasSigac {
    private static final String CARRERA = OrganizacionAcademica.CARRERA_BASE;
    private final PrintStream salida;
    private int aprobadas;
    private int fallidas;

    private PruebasSigac(PrintStream salida) {
        this.salida = salida;
    }

    public static void main(String[] args) {
        PrintStream salida = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        PruebasSigac suite = new PruebasSigac(salida);
        suite.ejecutar();
        if (suite.fallidas != 0) {
            System.exit(1);
        }
    }

    private void ejecutar() {
        salida.println("=== Pruebas automatizadas SIGAC-UTA ===");
        prueba("P01", "CRUD y edición visible en los índices y la jerarquía", () -> {
            Sigac app = new Sigac();
            app.registrarEstudiante(12, "Isabel", CARRERA);
            igual("[12 | Isabel | SOF]", app.estudiantes().listar().toString());
            app.actualizarNombre(12, "Isabel López");
            var consulta = app.estudiantes().compararBusquedas(12);
            igual("Isabel López", consulta.secuencial().valor().nombre());
            igual(consulta.secuencial().valor(), consulta.abb().valor());
            contiene(app.organizacion().lineasJerarquia().toString(), "Estudiante 12 - Isabel López");
            igual(12, app.eliminarEstudiante(12).matricula());
            igual("[]", app.estudiantes().listar().toString());
            igual("[]", app.organizacion().matriculasDeCarrera(CARRERA).toString());
        });
        prueba("P02", "Matrícula duplicada y baja de ID inexistente", () -> {
            Sigac app = CasoIntegrador.preparar();
            error(IllegalArgumentException.class, () -> app.registrarEstudiante(50, "Duplicado", CARRERA));
            error(IllegalArgumentException.class, () -> app.eliminarEstudiante(99));
            igual(7, app.estudiantes().listar().tamano());
            igual("Ana", nombre(app, 50));
            igual("[50, 30, 70, 20, 40, 60, 80]", app.organizacion().matriculasDeCarrera(CARRERA).toString());
        });
        prueba("P03", "Referencias nulas, campos vacíos e ID inválido", () -> {
            Sigac app = new Sigac();
            error(IllegalArgumentException.class, () -> app.registrarEstudiante(10, null, CARRERA));
            error(IllegalArgumentException.class, () -> app.registrarEstudiante(10, " ", CARRERA));
            error(IllegalArgumentException.class, () -> app.registrarEstudiante(10, "Ana", null));
            error(IllegalArgumentException.class, () -> app.registrarEstudiante(0, "Ana", CARRERA));
            error(IllegalArgumentException.class, () -> app.estudiantes().compararBusquedas(-1));
            igual(0, app.estudiantes().listar().tamano());
            igual("[]", app.estudiantes().inorden().toString());
        });
        prueba("P04", "Comparación de búsquedas exitosas y fallidas", () -> {
            Sigac app = CasoIntegrador.preparar();
            var existente = app.estudiantes().compararBusquedas(40);
            igual(5, existente.secuencial().comparaciones());
            igual(3, existente.abb().comparaciones());
            igual("Diego", existente.abb().valor().nombre());
            var ausente = app.estudiantes().compararBusquedas(99);
            igual(false, ausente.secuencial().encontrada());
            igual(false, ausente.abb().encontrada());
            igual(7, ausente.secuencial().comparaciones());
            igual(3, ausente.abb().comparaciones());
        });
        prueba("P05", "Lista enlazada: inicio, medio, fin y reinserción", () -> {
            ListaEnlazada<Integer> lista = new ListaEnlazada<>();
            for (int valor = 1; valor <= 4; valor++) {
                lista.agregar(valor);
            }
            igual(true, lista.eliminarPrimero(valor -> valor == 1));
            igual(true, lista.eliminarPrimero(valor -> valor == 3));
            igual(true, lista.eliminarPrimero(valor -> valor == 4));
            igual(false, lista.eliminarPrimero(valor -> valor == 9));
            igual("2", unir(lista));
            igual(true, lista.eliminarPrimero(valor -> valor == 2));
            lista.agregar(5);
            lista.agregar(6);
            igual("5, 6", unir(lista));
        });
        prueba("P06", "Vector: redimensionamiento, eliminación e índice inválido", () -> {
            Vector<Integer> vector = new Vector<>();
            for (int valor = 0; valor < 20; valor++) {
                vector.agregar(valor);
            }
            igual(20, vector.tamano());
            igual(10, vector.eliminar(10));
            igual(11, vector.obtener(10));
            igual(19, vector.obtener(18));
            error(IndexOutOfBoundsException.class, () -> vector.obtener(19));
        });
        prueba("P07", "ABB: preorden, inorden y posorden del caso obligatorio", () -> {
            var estudiantes = CasoIntegrador.preparar().estudiantes();
            igual("[50, 30, 20, 40, 70, 60, 80]", estudiantes.preorden().toString());
            igual("[20, 30, 40, 50, 60, 70, 80]", estudiantes.inorden().toString());
            igual("[20, 40, 30, 60, 80, 70, 50]", estudiantes.posorden().toString());
        });
        prueba("P08", "Eliminar 30 del ABB y mantener coherencia entre módulos", () -> {
            Sigac app = CasoIntegrador.preparar();
            app.eliminarEstudiante(30);
            igual("[20, 40, 50, 60, 70, 80]", app.estudiantes().inorden().toString());
            igual("[50, 40, 20, 70, 60, 80]", app.estudiantes().preorden().toString());
            igual("[50, 70, 20, 40, 60, 80]", app.organizacion().matriculasDeCarrera(CARRERA).toString());
            igual("50, 70, 20, 40, 60, 80", matriculas(app.estudiantes().listar()));
            var consulta = app.estudiantes().compararBusquedas(30);
            igual(false, consulta.secuencial().encontrada());
            igual(false, consulta.abb().encontrada());
            error(IllegalArgumentException.class, () -> app.social().amigosDe(30));
        });
        prueba("P09", "Eliminar raíz: sucesor profundo conserva su hijo derecho", () -> {
            ArbolBinarioBusqueda<Integer> arbol = new ArbolBinarioBusqueda<>();
            for (int clave : new int[]{50, 30, 80, 60, 70, 90}) {
                igual(true, arbol.insertar(clave, clave));
            }
            igual(false, arbol.insertar(80, 8));
            igual(true, arbol.eliminar(50));
            igual("[60, 30, 80, 70, 90]", arbol.preorden().toString());
            igual("[30, 60, 70, 80, 90]", arbol.inorden().toString());
            igual(70, arbol.buscar(70).valor());
            igual(false, arbol.buscar(50).encontrada());
        });
        prueba("P10", "ABB vacío, raíz con un hijo y eliminación de nodo único", () -> {
            ArbolBinarioBusqueda<Integer> arbol = new ArbolBinarioBusqueda<>();
            igual("[]", arbol.inorden().toString());
            igual(0, arbol.buscar(1).comparaciones());
            igual(false, arbol.eliminar(1));
            arbol.insertar(2, 2);
            arbol.insertar(1, 1);
            igual(true, arbol.eliminar(2));
            igual("[1]", arbol.preorden().toString());
            igual(true, arbol.eliminar(1));
            igual("[]", arbol.posorden().toString());
        });
        prueba("P11", "Árbol general con más de una facultad y carrera", () -> {
            Sigac app = new Sigac();
            app.organizacion().agregarFacultad("F2", "Facultad 2");
            app.organizacion().agregarCarrera("F2", "C2", "Carrera 2");
            app.organizacion().agregarCarrera("F2", "C3", "Carrera 3");
            app.registrarEstudiante(72, "Paula", "C2");
            igual("[72]", app.organizacion().matriculasDeCarrera("C2").toString());
            igual("[]", app.organizacion().matriculasDeCarrera("C3").toString());
            error(IllegalArgumentException.class, () -> app.organizacion().agregarCarrera("X", "C5", "Carrera 5"));
            error(IllegalArgumentException.class, () -> app.organizacion().agregarCarrera("F2", CARRERA, "Duplicada"));
            error(IllegalArgumentException.class, () -> app.registrarEstudiante(73, "Nora", "X"));
            igual(1, app.estudiantes().listar().tamano());
        });
        prueba("P12", "Cola FIFO: tres solicitudes en el orden de llegada", () -> {
            Sigac app = CasoIntegrador.preparar();
            app.registrarSolicitud(1, 50, "PC1");
            app.registrarSolicitud(2, 70, "PC2");
            app.registrarSolicitud(3, 20, "PC3");
            igual(3, app.atencion().listarPendientes().tamano());
            for (int esperado : new int[]{1, 2, 3}) {
                igual(esperado, app.atencion().atenderSiguiente().id());
            }
            igual(0, app.atencion().listarPendientes().tamano());
        });
        prueba("P13", "Cola vacía, llamada con nulo y nueva inserción", () -> {
            Cola<Integer> cola = new Cola<>();
            error(NoSuchElementException.class, cola::desencolar);
            error(NullPointerException.class, () -> cola.encolar(null));
            cola.encolar(1);
            igual(1, cola.desencolar());
            cola.encolar(2);
            igual(1, cola.tamano());
            igual(2, cola.desencolar());
            error(NoSuchElementException.class, new Sigac().atencion()::atenderSiguiente);
        });
        prueba("P14", "Pila LIFO: restaura dos cambios en orden inverso", () -> {
            Sigac app = CasoIntegrador.preparar();
            app.actualizarNombre(80, "Nombre corregido");
            app.actualizarNombre(80, "Nombre confirmado");
            igual("Nombre confirmado", nombre(app, 80));
            igual(2, app.cambiosPendientes());
            igual("Nombre corregido", app.deshacerCambio().nombre());
            igual("Nombre corregido", nombre(app, 80));
            igual("Estudiante 80", app.deshacerCambio().nombre());
            igual("Estudiante 80", nombre(app, 80));
            igual(0, app.cambiosPendientes());
        });
        prueba("P15", "Pila vacía y cambios sin efecto o con valor inválido", () -> {
            Sigac app = CasoIntegrador.preparar();
            error(NoSuchElementException.class, app::deshacerCambio);
            igual(false, app.actualizarNombre(50, " Ana "));
            error(IllegalArgumentException.class, () -> app.actualizarNombre(50, null));
            igual(0, app.cambiosPendientes());
            igual("Ana", nombre(app, 50));
        });
        prueba("P16", "Solicitudes duplicadas, IDs inexistentes y nulos", () -> {
            Sigac app = CasoIntegrador.preparar();
            error(IllegalArgumentException.class, () -> app.registrarSolicitud(1, 99, "PC1"));
            error(IllegalArgumentException.class, () -> app.registrarSolicitud(1, 50, "XXX"));
            error(IllegalArgumentException.class, () -> app.registrarSolicitud(1, 50, null));
            error(IllegalArgumentException.class, () -> app.registrarSolicitud(0, 50, "PC1"));
            app.registrarSolicitud(1, 50, "PC1");
            error(IllegalArgumentException.class, () -> app.registrarSolicitud(1, 50, "PC2"));
            igual(1, app.atencion().atenderSiguiente().id());
            error(IllegalArgumentException.class, () -> app.registrarSolicitud(1, 50, "PC1"));
            igual(0, app.atencion().listarPendientes().tamano());
        });
        prueba("P17", "Baja segura con solicitudes pendientes", () -> {
            Sigac app = CasoIntegrador.preparar();
            app.registrarSolicitud(1, 50, "PC1");
            error(IllegalArgumentException.class, () -> app.eliminarEstudiante(50));
            error(IllegalArgumentException.class, () -> app.eliminarEquipo("PC1"));
            igual("Ana", nombre(app, 50));
            igual(6, app.laboratorio().listarEquipos().tamano());
            app.atencion().atenderSiguiente();
            app.eliminarEstudiante(50);
            app.eliminarEquipo("PC1");
            igual(false, app.estudiantes().compararBusquedas(50).abb().encontrada());
            igual("[SW1, SRV1, PC2, PC3, PC4]", app.laboratorio().listarEquipos().toString());
        });
        prueba("P18", "Baja limpia amistades e historial sin alterar la pila restante", () -> {
            Sigac app = CasoIntegrador.preparar();
            app.actualizarNombre(50, "Ana 2");
            app.actualizarNombre(70, "Beto 2");
            app.actualizarNombre(50, "Ana 3");
            app.actualizarNombre(70, "Beto 3");
            app.eliminarEstudiante(50);
            igual("40", matriculas(app.social().amigosDe(70)));
            app.registrarEstudiante(50, "Nueva persona", CARRERA);
            igual(0, app.social().amigosDe(50).tamano());
            igual(2, app.cambiosPendientes());
            igual("Beto 2", app.deshacerCambio().nombre());
            igual("Beto", app.deshacerCambio().nombre());
            igual("Nueva persona", nombre(app, 50));
        });
        prueba("P19", "BFS, pesos y PC4 no alcanzable", () -> {
            var red = CasoIntegrador.preparar().laboratorio();
            igual("[PC1, SW1, SRV1, PC2, PC3, PC4]", red.listarEquipos().toString());
            igual("[PC1 -> SW1 (peso 2), PC1 -> PC3 (peso 1), SW1 -> SRV1 (peso 3), SRV1 -> PC2 (peso 2)]",
                    red.listarConexiones().toString());
            var resultado = red.bfs("PC1");
            igual("[PC1, SW1, PC3, SRV1, PC2]", resultado.visitados().toString());
            igual("[PC4]", resultado.noAlcanzables().toString());
            igual("[PC4]", red.bfs("PC4").visitados().toString());
            igual("[PC2]", red.bfs("PC2").visitados().toString());
        });
        prueba("P20", "DFS reproduce el orden de inserción de vecinos", () -> {
            var resultado = CasoIntegrador.preparar().laboratorio().dfs("PC1");
            igual("[PC1, SW1, SRV1, PC2, PC3]", resultado.visitados().toString());
            igual("[PC4]", resultado.noAlcanzables().toString());
        });
        prueba("P21", "Eliminar arista y actualizar ambos recorridos", () -> {
            var red = CasoIntegrador.preparar().laboratorio();
            red.desconectar("SW1", "SRV1");
            igual("[PC1, SW1, PC3]", red.bfs("PC1").visitados().toString());
            igual("[PC1, SW1, PC3]", red.dfs("PC1").visitados().toString());
            igual("[SRV1, PC2, PC4]", red.bfs("PC1").noAlcanzables().toString());
            igual("[SRV1, PC2, PC4]", red.dfs("PC1").noAlcanzables().toString());
        });
        prueba("P22", "Grafo con ciclos y caminos convergentes", () -> {
            var red = CasoIntegrador.preparar().laboratorio();
            red.conectar("PC3", "SRV1", 1);
            red.conectar("SRV1", "PC1", 1);
            red.conectar("PC2", "SW1", 1);
            red.conectar("PC3", "PC4", 1);
            igual("[PC1, SW1, PC3, SRV1, PC4, PC2]", red.bfs("PC1").visitados().toString());
            igual("[PC1, SW1, SRV1, PC2, PC3, PC4]", red.dfs("PC1").visitados().toString());
            igual("[]", red.bfs("PC1").noAlcanzables().toString());
        });
        prueba("P23", "Validación de vértices, aristas, pesos y red vacía", () -> {
            var red = CasoIntegrador.preparar().laboratorio();
            error(IllegalArgumentException.class, () -> red.registrarEquipo("PC1"));
            error(IllegalArgumentException.class, () -> red.registrarEquipo(null));
            error(IllegalArgumentException.class, () -> red.conectar("PC1", "SW1", 8));
            error(IllegalArgumentException.class, () -> red.conectar("PC1", "PC1", 8));
            error(IllegalArgumentException.class, () -> red.conectar("PC1", "PC4", 0));
            error(IllegalArgumentException.class, () -> red.conectar("PC1", "X", 1));
            error(IllegalArgumentException.class, () -> red.desconectar("PC3", "PC1"));
            error(IllegalArgumentException.class, () -> new Sigac().laboratorio().bfs("PC1"));
            igual(4, red.listarConexiones().tamano());
        });
        prueba("P24", "Eliminar equipo con aristas entrantes y salientes", () -> {
            Sigac app = CasoIntegrador.preparar();
            var red = app.laboratorio();
            red.conectar("PC3", "SW1", 1);
            red.conectar("SW1", "PC4", 1);
            app.eliminarEquipo("SW1");
            igual("[PC1 -> PC3 (peso 1), SRV1 -> PC2 (peso 2)]", red.listarConexiones().toString());
            igual("[PC1, PC3]", red.bfs("PC1").visitados().toString());
            red.registrarEquipo("SW1");
            red.conectar("PC3", "SW1", 7);
            igual("[PC1, PC3, SW1]", red.bfs("PC1").visitados().toString());
            igual("[SW1]", red.dfs("SW1").visitados().toString());
        });
        prueba("P25", "Ana: Diego con dos amigos en común y Elena con uno", () -> {
            var recomendaciones = CasoIntegrador.preparar().social().recomendar(50);
            igual(2, recomendaciones.tamano());
            igual("Diego", recomendaciones.obtener(0).estudiante().nombre());
            igual(2, recomendaciones.obtener(0).amigosEnComun());
            igual("Elena", recomendaciones.obtener(1).estudiante().nombre());
            igual(1, recomendaciones.obtener(1).amigosEnComun());
        });
        prueba("P26", "Exclusión de usuario, directos y contactos de tercer grado", () -> {
            var social = CasoIntegrador.preparar().social();
            social.agregarAmistad(50, 40);
            social.agregarAmistad(60, 30);
            var candidatos = social.recomendar(50);
            igual(1, candidatos.tamano());
            igual(60, candidatos.obtener(0).estudiante().matricula());
            igual(0, social.recomendar(80).tamano());
        });
        prueba("P27", "Desempate por matrícula y retirada de amistad recíproca", () -> {
            var social = CasoIntegrador.preparar().social();
            social.quitarAmistad(40, 70);
            igual("50", matriculas(social.amigosDe(70)));
            igual("20", matriculas(social.amigosDe(40)));
            var candidatos = social.recomendar(50);
            igual(2, candidatos.tamano());
            igual(40, candidatos.obtener(0).estudiante().matricula());
            igual(60, candidatos.obtener(1).estudiante().matricula());
            igual(1, candidatos.obtener(0).amigosEnComun());
            igual(1, candidatos.obtener(1).amigosEnComun());
        });
        prueba("P28", "Amistad duplicada, inexistente y autorrelación", () -> {
            var social = CasoIntegrador.preparar().social();
            error(IllegalArgumentException.class, () -> social.agregarAmistad(70, 50));
            error(IllegalArgumentException.class, () -> social.agregarAmistad(50, 50));
            error(IllegalArgumentException.class, () -> social.quitarAmistad(50, 40));
            error(IllegalArgumentException.class, () -> social.recomendar(99));
            igual("70, 20", matriculas(social.amigosDe(50)));
        });
        prueba("P29", "Caso obligatorio desde el menú y separado de la sesión", () -> {
            Sigac app = new Sigac();
            app.registrarEstudiante(15, "Estudiante local", CARRERA);
            String texto = ejecutarMenu(app, "6\n0\n");
            contiene(texto, "Inorden:  [20, 30, 40, 50, 60, 70, 80]");
            contiene(texto, "Inorden después de eliminar 30: [20, 40, 50, 60, 70, 80]");
            contiene(texto, "Atendida: S3");
            contiene(texto, "Segundo deshacer: 80 vuelve a Estudiante 80.");
            contiene(texto, "No alcanzables desde PC1: [PC4]");
            contiene(texto, "Diego | amigos en común: 2");
            contiene(texto, "Elena | amigos en común: 1");
            contiene(texto, "No alcanzables desde PC1: [SRV1, PC2, PC4]");
            contiene(texto, "Fin de sesión.");
            igual("[15]", app.estudiantes().inorden().toString());
            igual(texto, ejecutarMenu(app, "6\n0\n"));
        });
        prueba("P30", "Menú: entradas inválidas, CRUD interactivo y cola vacía", () -> {
            String entradas = String.join("\n", "abc", "99", "1", "1", "10", "Inés", CARRERA,
                    "3", "10", "4", "10", "Inés Luz", "0", "2", "4", "3", "0", "1", "5", "10", "0", "0", "");
            String texto = ejecutarMenu(new Sigac(), entradas);
            contiene(texto, "Error: Ingrese un número entero válido.");
            contiene(texto, "Error: Opción fuera del menú.");
            contiene(texto, "Estudiante registrado: 10 | Inés | SOF");
            contiene(texto, "ABB (1 comparación): 10 | Inés | SOF");
            contiene(texto, "Nombre actualizado.");
            contiene(texto, "Nombre restaurado: 10 | Inés | SOF");
            contiene(texto, "Error: La cola está vacía.");
            contiene(texto, "Estudiante eliminado: 10 | Inés | SOF");
            contiene(texto, "Fin de sesión.");
        });
        prueba("P31", "El fin de entrada durante un formulario cierra la sesión", () -> {
            Sigac app = new Sigac();
            String texto = ejecutarMenu(app, "1\n1\n21\n");
            contiene(texto, "Fin de sesión.");
            igual(0, app.estudiantes().listar().tamano());
        });
        salida.printf("%nRESULTADO: %d aprobadas de %d | %d fallidas%n", aprobadas, aprobadas + fallidas, fallidas);
    }

    private void prueba(String codigo, String nombre, Runnable caso) {
        try {
            caso.run();
            aprobadas++;
            salida.println("[OK] " + codigo + " | " + nombre);
        } catch (RuntimeException | AssertionError fallo) {
            fallidas++;
            salida.println("[FALLO] " + codigo + " | " + nombre + " | " + fallo);
        }
    }

    private static String nombre(Sigac app, int matricula) {
        return app.estudiantes().compararBusquedas(matricula).abb().valor().nombre();
    }

    private static String matriculas(Iterable<Estudiante> estudiantes) {
        StringJoiner resultado = new StringJoiner(", ");
        for (Estudiante estudiante : estudiantes) {
            resultado.add(Integer.toString(estudiante.matricula()));
        }
        return resultado.toString();
    }

    private static String unir(Iterable<?> valores) {
        StringJoiner resultado = new StringJoiner(", ");
        for (Object valor : valores) {
            resultado.add(String.valueOf(valor));
        }
        return resultado.toString();
    }

    private static String ejecutarMenu(Sigac app, String comandos) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner entrada = new Scanner(comandos);
                PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            new Menu(app, entrada, salida).ejecutar();
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static void igual(Object esperado, Object obtenido) {
        if (!Objects.equals(esperado, obtenido)) {
            throw new AssertionError("Esperado <" + esperado + "> pero se obtuvo <" + obtenido + ">.");
        }
    }

    private static void contiene(String texto, String fragmento) {
        if (!texto.contains(fragmento)) {
            throw new AssertionError("La salida no contiene: " + fragmento);
        }
    }

    private static void error(Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException obtenida) {
            if (tipo.isInstance(obtenida)) {
                return;
            }
            throw new AssertionError("Se esperaba " + tipo.getSimpleName() + " pero ocurrió " + obtenida, obtenida);
        }
        throw new AssertionError("Se esperaba una excepción " + tipo.getSimpleName() + ".");
    }
}
