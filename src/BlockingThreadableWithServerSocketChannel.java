import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by JP16163 on 2014/08/12.
 */
public class BlockingThreadableWithServerSocketChannel {

    // single thread
    public static void main(String[] args) throws IOException, InterruptedException {

        ExecutorService worker = Executors.newCachedThreadPool();

        try (ServerSocketChannel listener = ServerSocketChannel.open();) {
            listener.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            listener.bind(new InetSocketAddress(8081));

            System.out.println("Server listening on port 8081...");

            while (true) {
                final SocketChannel _channel = listener.accept();
                System.out.printf("ACCEPT %s%n", _channel);

                worker.submit(new Runnable() {
                    @Override
                    public void run() {
                        try (SocketChannel channel = _channel;) {
                            System.out.printf("CLOSE %s%n", channel);
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
