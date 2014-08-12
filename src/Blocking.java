import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by JP16163 on 2014/08/12.
 */
public class Blocking {

    public static void main(String[] args) throws IOException, InterruptedException {

        try (ServerSocket listener = new ServerSocket();) {
            listener.setReuseAddress(true);
            listener.bind(new InetSocketAddress(8080));
            System.out.println("Server listening on port 8080...");
            while (true) {
                try (Socket socket = listener.accept();) {
                    InputStream from = socket.getInputStream();
                    OutputStream to = socket.getOutputStream();
                    System.out.printf("CLOSE %s%n", from);
                    Thread.sleep(10000);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
