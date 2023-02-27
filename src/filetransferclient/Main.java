
package filetransferclient;

/**
 *
 * @author Jose Alfredo Nuñez Aguirre
 */
public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(new Client());
        thread.start();
    }
    
}
