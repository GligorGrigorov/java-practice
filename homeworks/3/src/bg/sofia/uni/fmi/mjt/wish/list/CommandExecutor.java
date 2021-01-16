package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.storage.Storage;

import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.Set;

public class CommandExecutor {
    private final Storage storage;

    public CommandExecutor(Storage storage) {
        this.storage = storage;
    }

    String executeCommand(String replyFromClient, SocketChannel currentChannel) {
        String replyToClient;
        if (!isValidCommand(replyFromClient)) {
            replyToClient = "[ Unknown command ]";
        }
        String[] tokens = replyFromClient.split(" ");
        switch (tokens[0]) {
            case "register" -> replyToClient = register(tokens[1], tokens[2], currentChannel);
            case "login" -> replyToClient = login(tokens[1], tokens[2], currentChannel);
            case "get-wish" -> {
                if (!storage.isLoggedInUserOnThisChannel(currentChannel)) {
                    replyToClient = "[ You are not logged in ]";
                } else {
                    replyToClient = getWish(currentChannel);
                }
            }
            case "logout" -> replyToClient = logout(currentChannel);
            case "post-wish" -> {
                if (!storage.isLoggedInUserOnThisChannel(currentChannel)) {
                    replyToClient = "[ You are not logged in ]";
                } else {
                    String name = tokens[1];
                    String wish = replyFromClient.split(tokens[1] + " ")[1];
                    replyToClient = postWish(name, wish);
                }
            }
            case "disconnect" -> {
                logout(currentChannel);
                replyToClient = "[ Disconnected from server ]";
            }
            default -> replyToClient = "[ Unknown command ]";
        }
        return replyToClient;
    }

    private boolean isValidCommand(String reply) {
        String[] tokens = reply.split(" ");
        return switch (tokens[0]) {
            case "post-wish" -> tokens.length >= 3;
            case "login", "register" -> tokens.length == 3;
            case "logout", "get-wish", "disconnect" -> tokens.length == 1;
            default -> false;
        };
    }

    private boolean isValidUsername(String username) {
        return username.chars()
                .filter(x -> Character.isLetterOrDigit(x) || x == '-' || x == '_' || x == '.')
                .count() == username.length();
    }

    private String postWish(String name, String wish) {
        String replyToClient;
        if (!storage.isRegisteredUser(name)) {
            replyToClient = "[ Student with username " + name + " is not registered ]";
            return replyToClient;
        }
        if (storage.containsThisWishForUser(name, wish)) {
            replyToClient = "[ The same gift for student " + name + " was already submitted ]";
            return replyToClient;
        }
        storage.addWishForStudent(name, wish);
        replyToClient = "[ Gift " + wish + " for student " + name + " submitted successfully ]";
        return replyToClient;
    }

    private String getWish(SocketChannel currentChannel) {
        String replyToClient;
        if (storage.getNumberOfStudentsWithWishes() == 0 || storage.getNumberOfStudentsWithWishes() == 1
                && storage.isThereAWishForUser(storage.getLoggedInUserOnThisChannel(currentChannel))) {
            replyToClient = "[ There are no students present in the wish list ]";
            return replyToClient;
        }
        Random random = new Random();
        String randomName;
        do {
            int randomIndex = random.nextInt(storage.getNumberOfStudentsWithWishes());
            randomName = (String) storage.getAllWishes().keySet().toArray()[randomIndex];
        } while (storage.isLoggedInUserOnThisChannel(currentChannel)
                && storage.getLoggedInUserOnThisChannel(currentChannel).equals(randomName));
        Set<String> wishList = storage.getWishesForUser(randomName);
        storage.removeWishesForUser(randomName);
        String listOutput = String.join(", ", wishList);
        replyToClient = "[ " + randomName + ": [" + listOutput + "] ]";
        return replyToClient;
    }

    private String register(String username, String password, SocketChannel currentChannel) {
        if (!isValidUsername(username)) {
            return "[ Username " + username + " is invalid, select a valid one ]";
        }
        if (storage.isRegisteredUser(username)) {
            return "[ Username " + username + " is already taken, select another one ]";
        }
        storage.addRegisteredUser(username, password);
        storage.addLoggedInUser(username, currentChannel);
        return "[ Username " + username + " successfully registered ]";
    }

    private String login(String username, String password, SocketChannel currentChannel) {
        if (storage.isRegisteredUser(username) && storage.getUserPassword(username).compareTo(password) == 0) {
            storage.addLoggedInUser(username, currentChannel);
            return "[ User " + username + " successfully logged in ]";
        }
        return "[ Invalid username/password combination ]";
    }

    private String logout(SocketChannel currentChannel) {
        if (storage.isLoggedInUserOnThisChannel(currentChannel)) {
            storage.removeLoggedInUser(currentChannel);
            return "[ Successfully logged out ]";
        }
        return "[ You are not logged in ]";
    }
}
