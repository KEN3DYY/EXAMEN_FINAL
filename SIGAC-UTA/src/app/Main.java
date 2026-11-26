package app;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import servicios.Sigac;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        PrintStream salida = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        if (args.length == 1 && "--demo".equals(args[0])) {
            CasoIntegrador.ejecutar(salida);
        } else if (args.length == 0) {
            try (Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8)) {
                new Menu(new Sigac(), entrada, salida).ejecutar();
            }
        } else {
            System.err.println("Uso: java -cp out app.Main [--demo]");
            System.exit(2);
        }
    }
}
