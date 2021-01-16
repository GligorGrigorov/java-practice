package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WishListClientThread extends Thread {
    private final int serverPort;
    private final String serverHost;
    private static final ByteBuffer BUFFER = ByteBuffer.allocateDirect(512);

    public WishListClientThread(String host, int port) {
        serverHost = host;
        serverPort = port;
    }

    @Override
    public void run() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
            System.out.println("Connected to the server.");
            while (true) {
                System.out.print("=> ");
                String message = scanner.nextLine();
                message = message + System.lineSeparator();
                BUFFER.clear();
                BUFFER.put(message.getBytes());
                BUFFER.flip();
                socketChannel.write(BUFFER);

                BUFFER.clear();
                socketChannel.read(BUFFER);
                BUFFER.flip();

                byte[] byteArray = new byte[BUFFER.remaining()];
                BUFFER.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8);
                System.out.println(reply);
                if (reply.equals("[ Disconnected from server ]" + System.lineSeparator())) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.print("IO exception was thrown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
