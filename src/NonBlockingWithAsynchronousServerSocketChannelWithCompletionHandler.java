import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by JP16163 on 2014/08/12.
 */
public class NonBlockingWithAsynchronousServerSocketChannelWithCompletionHandler {

    private AsynchronousServerSocketChannel serverChannel;

    public void start() throws IOException {
        System.out.println(String.format("start: name: %s", Thread.currentThread().getName()));
        serverChannel = AsynchronousServerSocketChannel.open();
        serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverChannel.bind(new InetSocketAddress(8000));
        serverChannel.accept(serverChannel, new Acceptor());
    }

    class Acceptor implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

        private final ByteBuffer buffer = ByteBuffer.allocate(1024);

        public Acceptor(){
            System.out.println("an acceptor has created.");
        }

        public void completed(final AsynchronousSocketChannel channel, AsynchronousServerSocketChannel serverChannel) {
            System.out.println(String.format("write: name: %s", Thread.currentThread().getName()));
            channel.read(buffer, channel, new Reader(buffer));
            serverChannel.accept(serverChannel, new Acceptor());
        }

        public void failed(Throwable exception, AsynchronousServerSocketChannel serverChannel) {
            throw new RuntimeException(exception);
        }
    }

    class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        private ByteBuffer buffer;

        public Reader(ByteBuffer buffer){
            this.buffer = buffer;
        }

        public void completed(Integer result, AsynchronousSocketChannel channel){
            System.out.println(String.format("read: name: %s", Thread.currentThread().getName()));
            if(result != null && result < 0){
                try{
                    channel.close();
                    return;
                }catch(IOException ignore){}
            }
            buffer.flip();
            channel.write(buffer, channel, new Writer(buffer));
        }
        public void failed(Throwable exception, AsynchronousSocketChannel channel){
            throw new RuntimeException(exception);
        }
    }

    class Writer implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        private ByteBuffer buffer;

        public Writer(ByteBuffer buffer){
            this.buffer = buffer;
        }

        public void completed(Integer result, AsynchronousSocketChannel channel) {
            System.out.println(String.format("write: name: %s", Thread.currentThread().getName()));
            buffer.clear();
            channel.read(buffer, channel, new Reader(buffer));
        }

        public void failed(Throwable exception, AsynchronousSocketChannel channel) {
            throw new RuntimeException(exception);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException{
        new NonBlockingWithAsynchronousServerSocketChannelWithCompletionHandler().start();
        while(true){
            Thread.sleep(1000L);
        }
    }
}
