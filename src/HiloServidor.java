import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HiloServidor extends Thread{
    private boolean finHiloServidor = true;
    private DatagramSocket socket;
    private static Usuario[] usuarios = new Usuario[2];
    private static int cantidadUsuarios = 0;


    public HiloServidor(){
        try {
            socket = new DatagramSocket(25565);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(!finHiloServidor){
            byte[] datos = new byte[1024];
            DatagramPacket dp = new DatagramPacket(datos, datos.length);
            try {
                socket.receive(dp);
                procesarMensaje(dp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void procesarMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData(), 0, dp.getLength()).trim();
        String[] partes = msg.split("_");
        String comando = partes[0];

        if (comando.equals("Permiso")) {
            if (cantidadUsuarios < 2) {
                String nombre = partes.length > 1 ? partes[1] : "Red" + (cantidadUsuarios + 1);
                String pkNombre = partes.length > 2 ? partes[2] : "Pikachu";
                int hp = partes.length > 3 ? Integer.parseInt(partes[3]) : 1000;
                int nivel = partes.length > 4 ? Integer.parseInt(partes[4]) : 250;
                String tipo = partes.length > 5 ? partes[5] : "planta";

                Usuario nuevo = new Usuario(nombre, dp.getAddress(), dp.getPort(), pkNombre, hp, nivel, tipo);
                usuarios[cantidadUsuarios++] = nuevo;

                System.out.println("[REGISTRO] " + nombre + " conectado desde " + dp.getAddress() + ":" + dp.getPort());
                enviarMensaje("CONECTADO_Bienvenido " + nombre, dp.getAddress(), dp.getPort());

                if (cantidadUsuarios == 2) {
                    enviarMensajeATodos("NOTIFICACION_¡La batalla entre " + usuarios[0].getNombre() + " y " + usuarios[1].getNombre() + " ha comenzado!");
                } else {
                    enviarMensaje("ERROR_Servidor lleno", dp.getAddress(), dp.getPort());
                }
                return;
            }
        }

        if (comando.equals("ATAQUE")) {
                Usuario atacante = obtenerUsuarioPorAddr(dp.getAddress(), dp.getPort());
                if (atacante == null) return;

                String tipoAtaque = partes[1].toLowerCase();
                boolean esCritico = Boolean.parseBoolean(partes[2]);
                int nivel = Integer.parseInt(partes[3]);
                boolean cambioPokemon = Boolean.parseBoolean(partes[4]);
                String nombrePk = partes[5];

                if (cambioPokemon) {
                    atacante.setPokeActual(nombrePk);
                }

                for (Usuario rival : usuarios) {
                    if (rival != null && rival != atacante && rival.getHp() > 0) {
                        int dano = calcularDano(nivel, tipoAtaque, esCritico, rival.getTipo());
                        rival.setHp(rival.getHp() - dano);

                        String notificacion = "NOTIFICACION_" + atacante.getNombre() + " atacó con " + nombrePk + " causando " + dano + " de daño a " + rival.getPokeActual() + ". HP restante: " + rival.getHp();

                        enviarMensajeATodos(notificacion);

                        if (rival.getHp() == 0) {
                            enviarMensajeATodos("NOTIFICACION_Se papearon a " + rival.getPokeActual());
                        }
                    }
                }
        }
    }

    private Usuario obtenerUsuarioPorAddr(InetAddress ip, int puerto) {
        for (Usuario u : usuarios) {
            if (u != null && u.getIp().equals(ip) && u.getPuerto() == puerto) {
                return u;
            }
        }
        return null;
    }

    private int calcularDano(int nivel, String tipoAtaque, boolean esCritico, String tipoDefensor) {
        double baseDano = nivel * 2.0;
        double multTipo = 1.0;

        if (tipoAtaque.equals("fuego")) {
            if (tipoDefensor.equals("planta")) multTipo = 2.0;
            else if (tipoDefensor.equals("agua")) multTipo = 0.5;
        } else if (tipoAtaque.equals("planta")) {
            if (tipoDefensor.equals("agua")) multTipo = 2.0;
            else if (tipoDefensor.equals("fuego")) multTipo = 0.5;
        } else if (tipoAtaque.equals("agua")) {
            if (tipoDefensor.equals("fuego")) multTipo = 2.0;
            else if (tipoDefensor.equals("planta")) multTipo = 0.5;
        }

        double multCritico = esCritico ? 1.5 : 1.0;
        return Math.max(1, (int) (baseDano * multTipo * multCritico));
    }

    public void enviarMensaje(String msg, InetAddress ip, int puerto){
        byte[] data = msg.getBytes();
        DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
        try {
            socket.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarMensajeATodos(String msg) {
        for (Usuario u : usuarios) {
            if (u != null) {
                enviarMensaje(msg, u.getIp(), u.getPuerto());
            }
        }
    }
}
