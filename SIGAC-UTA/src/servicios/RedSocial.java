package servicios;

import estructuras.Grafo;
import estructuras.Vector;
import modelo.Estudiante;

public final class RedSocial {
    public record Recomendacion(Estudiante estudiante, int amigosEnComun) {}

    private final Grafo<Integer> amistades = new Grafo<>(false);
    private final GestionEstudiantes estudiantes;

    RedSocial(GestionEstudiantes estudiantes) {
        this.estudiantes = estudiantes;
    }

    void registrar(int matricula) {
        amistades.agregarVertice(matricula);
    }

    void retirar(int matricula) {
        amistades.eliminarVertice(matricula);
    }

    public void agregarAmistad(int unaMatricula, int otraMatricula) {
        amistades.conectar(unaMatricula, otraMatricula, 1);
    }

    public void quitarAmistad(int unaMatricula, int otraMatricula) {
        amistades.desconectar(unaMatricula, otraMatricula);
    }

    public Vector<Estudiante> amigosDe(int matricula) {
        Vector<Estudiante> amigos = new Vector<>();
        int usuario = amistades.posicionDe(matricula);
        for (int posicion : amistades.posicionesVecinas(usuario)) {
            amigos.agregar(estudiantes.exigir(amistades.identificadorEn(posicion)));
        }
        return amigos;
    }

    public Vector<Recomendacion> recomendar(int matricula) {
        int usuario = amistades.posicionDe(matricula);
        int total = amistades.cantidadVertices();
        boolean[] excluido = new boolean[total];
        int[] coincidencias = new int[total];
        Vector<Integer> amigos = amistades.posicionesVecinas(usuario);
        excluido[usuario] = true;
        for (int amigo : amigos) {
            excluido[amigo] = true;
        }
        for (int amigo : amigos) {
            for (int candidato : amistades.posicionesVecinas(amigo)) {
                if (!excluido[candidato]) {
                    coincidencias[candidato]++;
                }
            }
        }
        Vector<Recomendacion> resultados = new Vector<>();
        for (int posicion = 0; posicion < total; posicion++) {
            if (coincidencias[posicion] > 0) {
                Estudiante candidato = estudiantes.exigir(amistades.identificadorEn(posicion));
                resultados.agregar(new Recomendacion(candidato, coincidencias[posicion]));
            }
        }
        ordenar(resultados);
        return resultados;
    }

    private void ordenar(Vector<Recomendacion> resultados) {
        for (int i = 1; i < resultados.tamano(); i++) {
            Recomendacion actual = resultados.obtener(i);
            int posicion = i;
            while (posicion > 0 && precede(actual, resultados.obtener(posicion - 1))) {
                resultados.reemplazar(posicion, resultados.obtener(posicion - 1));
                posicion--;
            }
            resultados.reemplazar(posicion, actual);
        }
    }

    private boolean precede(Recomendacion primera, Recomendacion segunda) {
        if (primera.amigosEnComun() != segunda.amigosEnComun()) {
            return primera.amigosEnComun() > segunda.amigosEnComun();
        }
        return primera.estudiante().matricula() < segunda.estudiante().matricula();
    }
}
