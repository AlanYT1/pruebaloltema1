import java.io.IOException;
import java.net.*;

public class HiloCliente extends Thread {
    private DatagramSocket conexion;
    private InetAddress ipServer;
    private int puerto = 25565;
    private boolean finHiloCliente;

    private String entrenador;
    private String pokemonActual;
    private int nivelActual;

    public HiloCliente(String entrenador, String pokemonActual, int hp, int nivel, String tipo) {
        this.entrenador = entrenador;
        this.pokemonActual = pokemonActual;
        this.nivelActual = nivel;
        this.finHiloCliente = false;

        try {
            System.out.println("Intentando conectar con el servidor Juez...");
            ipServer = InetAddress.getByName("255.255.255.255");
            conexion = new DatagramSocket();
            conexion.setBroadcast(true);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException("Error al inicializar la conexión UDP", e);
        }

        String mensajeRegistro = String.format("Permiso_%s_%s_%d_%d_%s",
                entrenador, pokemonActual, hp, nivel, tipo);
        enviarMensaje(mensajeRegistro);
    }

    public String getPokemonActual() { return pokemonActual; }
    public void setPokemonActual(String pokemonActual) { this.pokemonActual = pokemonActual; }
    public int getNivelActual() { return nivelActual; }

    public void enviarMensaje(String msg) {
        byte[] data = msg.getBytes();
        DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
        try {
            conexion.send(dp);
        } catch (IOException e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
        }
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData(), 0, dp.getLength()).trim();
        String[] mensajeCompuesto = msg.split("_");

        if (mensajeCompuesto[0].equals("CONECTADO")) {
            System.out.println("\n[SERVIDOR]: " + mensajeCompuesto[1]);
        } else if (mensajeCompuesto[0].equals("NOTIFICACION")) {
            System.out.println("[NOTIFICACIÓN JUEZ]: " + mensajeCompuesto[1]);
        }
    }

    @Override
    public void run() {
        while (!finHiloCliente) {
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                conexion.receive(dp);
                procesarMensaje(dp);
            } catch (IOException e) {
                if (finHiloCliente) break;
            }
        }
    }

    public void desconectar() {
        this.finHiloCliente = true;
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }
}
