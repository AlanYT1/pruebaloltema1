public class Servidor {
    public static HiloServidor hs;
    public static void main(String[] args){
        System.out.println("Arrancando");
        hs = new HiloServidor();
        hs.start();
    }
}
