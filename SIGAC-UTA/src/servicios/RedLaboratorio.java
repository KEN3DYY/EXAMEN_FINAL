package servicios;

import estructuras.Grafo;
import estructuras.Vector;
import modelo.Estudiante;

public final class RedLaboratorio {
    private final Grafo<String> equipos = new Grafo<>(true);

    public void registrarEquipo(String codigo) {
        equipos.agregarVertice(validarCodigo(codigo));
    }

    void retirarEquipo(String codigo) {
        equipos.eliminarVertice(codigo);
    }

    String exigirEquipo(String codigo) {
        return equipos.exigirId(validarCodigo(codigo));
    }

    public void conectar(String origen, String destino, int peso) {
        equipos.conectar(validarCodigo(origen), validarCodigo(destino), peso);
    }

    public void desconectar(String origen, String destino) {
        equipos.desconectar(validarCodigo(origen), validarCodigo(destino));
    }

    public Grafo.Recorrido<String> bfs(String origen) {
        return equipos.bfs(validarCodigo(origen));
    }

    public Grafo.Recorrido<String> dfs(String origen) {
        return equipos.dfs(validarCodigo(origen));
    }

    public Vector<String> listarEquipos() {
        return equipos.listarVertices();
    }

    public Vector<Grafo.Conexion<String>> listarConexiones() {
        return equipos.listarConexiones();
    }

    private String validarCodigo(String codigo) {
        return Estudiante.textoObligatorio(codigo, "El codigo de equipo");
    }
}
