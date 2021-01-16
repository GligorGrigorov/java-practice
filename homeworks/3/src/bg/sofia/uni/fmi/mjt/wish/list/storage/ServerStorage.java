package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerStorage implements Storage {
    private final Map<String, Set<String>> wishes;
    private final Map<String, String> registeredUsers;
    private final Map<SocketChannel, String> loggedInUsers;

    public ServerStorage() {
        wishes = new HashMap<>();
        loggedInUsers = new HashMap<>();
        registeredUsers = new HashMap<>();
    }

    @Override
    public boolean isLoggedInUserOnThisChannel(SocketChannel channel) {
        return loggedInUsers.containsKey(channel);
    }

    @Override
    public boolean isRegisteredUser(String username) {
        return registeredUsers.containsKey(username);
    }

    @Override
    public boolean isThereAWishForUser(String username) {
        return wishes.containsKey(username);
    }

    @Override
    public boolean containsThisWishForUser(String username, String wish) {
        if (wishes.containsKey(username)) {
            return wishes.get(username).contains(wish);
        }
        return false;
    }

    @Override
    public void addWishForStudent(String username, String wish) {
        if (!wishes.containsKey(username)) {
            wishes.put(username, new HashSet<>());
        }
        wishes.get(username).add(wish);
    }

    @Override
    public String getUserPassword(String user) {
        return registeredUsers.get(user);
    }

    @Override
    public void addLoggedInUser(String username, SocketChannel channel) {
        loggedInUsers.put(channel, username);
    }

    @Override
    public void removeLoggedInUser(SocketChannel channel) {
        loggedInUsers.remove(channel);
    }

    @Override
    public void addRegisteredUser(String username, String password) {
        registeredUsers.put(username, password);
    }

    @Override
    public int getNumberOfStudentsWithWishes() {
        return wishes.size();
    }

    @Override
    public String getLoggedInUserOnThisChannel(SocketChannel channel) {
        return loggedInUsers.get(channel);
    }

    @Override
    public Map<String, Set<String>> getAllWishes() {
        return wishes;
    }

    @Override
    public Set<String> getWishesForUser(String username) {
        return wishes.get(username);
    }

    @Override
    public void removeWishesForUser(String username) {
        wishes.remove(username);
    }
}
