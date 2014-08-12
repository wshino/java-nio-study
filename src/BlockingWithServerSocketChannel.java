import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by JP16163 on 2014/08/12.
 */
public class BlockingWithServerSocketChannel {

    // single thread
    public static void main(String[] args) throws IOException, InterruptedException {

        try (ServerSocketChannel listener = ServerSocketChannel.open();) {
            listener.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            listener.bind(new InetSocketAddress(8080));
            System.out.println("Server listening on port 8080...");
            while (true) {
                try (SocketChannel channel = listener.accept();) {
                    System.out.printf("CLOSE %s%n", channel);
                    Thread.sleep(10000);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
