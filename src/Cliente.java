import java.util.Random;
import java.util.Scanner;

public class Cliente {
    public static HiloCliente hc;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("POKEMON SHOWDOWN");
        System.out.print("Ingresá tu nombre de entrenador: ");
        String entrenador = scanner.nextLine().trim();

        System.out.print("Nombre de tu Pokémon inicial: ");
        String pokemonInicial = scanner.nextLine().trim();

        System.out.print("Tipo de tu Pokémon (fuego/agua/planta): ");
        String tipoPokemon = scanner.nextLine().trim().toLowerCase();

        int hp = 100;
        int nivel = 25;

        hc = new HiloCliente(entrenador, pokemonInicial, hp, nivel, tipoPokemon);
        hc.start();

        Random random = new Random();

        while (true) {
            System.out.println("\nMenu");
            System.out.println("1. Atacar");
            System.out.println("2. Cambiar Pokémon");
            System.out.println("3. Salir");
            System.out.print("Elegí una opción: ");

            String opcion = scanner.nextLine().trim();

            if (opcion.equals("1")) {
                System.out.print("Tipo de ataque (fuego/agua/planta): ");
                String tipoAtaque = scanner.nextLine().trim().toLowerCase();

                boolean esCritico = random.nextInt(3) == 0;

                String mensajeAtaque = String.format("ATAQUE_%s_%b_%d_false_%s", tipoAtaque, esCritico, hc.getNivelActual(), hc.getPokemonActual());

                hc.enviarMensaje(mensajeAtaque);
                System.out.println("Ataque enviado al servidor");

            } else if (opcion.equals("2")) {
                System.out.print("Nombre del nuevo Pokémon: ");
                String nuevoPokemon = scanner.nextLine().trim();
                hc.setPokemonActual(nuevoPokemon);

                String mensajeCambio = String.format("ATAQUE_ninguno_false_%d_true_%s", hc.getNivelActual(), nuevoPokemon);
                hc.enviarMensaje(mensajeCambio);

                System.out.println("Cambiaste a " + nuevoPokemon);

            } else if (opcion.equals("3")) {
                System.out.println("Saliendo de la partida");
                hc.desconectar();
                break;
            }
        }
        scanner.close();
        System.exit(0);
    }
}
