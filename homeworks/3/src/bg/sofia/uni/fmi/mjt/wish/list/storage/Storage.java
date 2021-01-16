package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

public interface Storage {
    boolean isLoggedInUserOnThisChannel(SocketChannel channel);

    boolean isRegisteredUser(String username);

    boolean isThereAWishForUser(String username);

    boolean containsThisWishForUser(String username, String wish);

    void addWishForStudent(String username, String wish);

    String getUserPassword(String user);

    void addLoggedInUser(String username, SocketChannel channel);

    void removeLoggedInUser(SocketChannel channel);

    void addRegisteredUser(String username, String password);

    int getNumberOfStudentsWithWishes();

    String getLoggedInUserOnThisChannel(SocketChannel channel);

    Map<String, Set<String>> getAllWishes();

    Set<String> getWishesForUser(String username);

    void removeWishesForUser(String username);
}
