package bg.sofia.uni.fmi.mjt.wish.list;

public class WishListClient {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";

    public static void main(String[] args) {
        WishListClientThread client = new WishListClientThread(SERVER_HOST, SERVER_PORT);
        client.start();
        try {
            client.join();
        } catch (InterruptedException e) {
            System.err.print("InterruptedException was thrown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
