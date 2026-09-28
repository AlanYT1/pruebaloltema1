import java.net.InetAddress;

public class Usuario {
    private String nombre;
    private InetAddress ip;
    private int puerto;
    private String pokeActual;
    private int hp;
    private int lvl;
    private String tipo;

    public Usuario(String nombre, InetAddress ip, int puerto, String pokeActual, int hp, int lvl, String tipo){
        this.nombre = nombre;
        this.ip = ip;
        this.puerto = puerto;
        this.pokeActual = pokeActual;
        this.hp = hp;
        this.lvl = lvl;
        this.tipo = tipo;
    }

    public String getNombre() {return nombre;}
    public InetAddress getIp() {return ip;}
    public int getPuerto() {return puerto;}
    public String getPokeActual() {return pokeActual;}
    public void setPokeActual(String pokeActual) {this.pokeActual = pokeActual;}
    public int getHp() {return hp;}
    public void setHp(int hp) {this.hp = Math.max(0, hp);}
    public int getLvl() {return lvl;}
    public String getTipo() {return tipo;}
}
