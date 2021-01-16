package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.storage.Storage;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandExecutorTest {
    private Storage storage;
    private SocketChannel channel;
    private CommandExecutor cmdExecutor;
    private static final String USR_1 = "gligor";
    private static final String USR_2 = "Gligor";
    private static final String WISH_1 = "kolelo";
    private static final String REG_USR_1 = "register gligor Abcd1234";
    private static final String GET_WISH = "get-wish";
    private static final String POST_WISH_FOR_USR_2 = "post-wish Gligor kolelo";

    @Before
    public void setUp() {
        storage = mock(Storage.class);
        try {
            channel = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cmdExecutor = new CommandExecutor(storage);
    }

    @Test
    public void testCmdExecReturnsSuccessfullyRegisteredUser() {
        when(storage.isRegisteredUser(USR_1)).thenReturn(false);
        String response = "[ Username gligor successfully registered ]";
        assertEquals(response, cmdExecutor.executeCommand(REG_USR_1, channel));
    }

    @Test
    public void testCmdExecReturnsAlreadyRegisteredUser() {
        when(storage.isRegisteredUser(USR_1)).thenReturn(true);
        String response = "[ Username gligor is already taken, select another one ]";
        assertEquals(response, cmdExecutor.executeCommand(REG_USR_1, channel));
    }

    @Test
    public void testCmdExecAutoLoginAfterRegistration() {
        when(storage.isRegisteredUser(USR_1)).thenReturn(true);
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(true);
        when(storage.getNumberOfStudentsWithWishes()).thenReturn(0);
        String response = "[ There are no students present in the wish list ]";
        assertEquals(response, cmdExecutor.executeCommand(GET_WISH, channel));
    }

    @Test
    public void testCmdExecWithInvalidUsername() {
        String request = "register Ko$io Abcd1234";
        String response = "[ Username Ko$io is invalid, select a valid one ]";
        assertEquals(response, cmdExecutor.executeCommand(request, channel));
    }

    @Test
    public void testUsernameAlreadyTaken() {
        when(storage.isRegisteredUser(USR_1)).thenReturn(true);
        String response = "[ Username gligor is already taken, select another one ]";
        assertEquals(response, cmdExecutor.executeCommand(REG_USR_1, channel));
    }

    @Test
    public void testPostWish() {
        when(storage.containsThisWishForUser(USR_2, WISH_1)).thenReturn(false);
        when(storage.isRegisteredUser(USR_2)).thenReturn(true);
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(true);
        String response = "[ Gift kolelo for student Gligor submitted successfully ]";
        assertEquals(response, cmdExecutor.executeCommand(POST_WISH_FOR_USR_2, channel));
    }

    @Test
    public void testTheSameWishAlreadySubmitted() {
        when(storage.containsThisWishForUser(USR_2, WISH_1)).thenReturn(true);
        when(storage.isRegisteredUser(USR_2)).thenReturn(true);
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(true);
        String response = "[ The same gift for student Gligor was already submitted ]";
        assertEquals(response, cmdExecutor.executeCommand(POST_WISH_FOR_USR_2, channel));
    }

    @Test
    public void testUserIsNotRegistered() {
        when(storage.containsThisWishForUser(USR_2, WISH_1)).thenReturn(true);
        when(storage.isRegisteredUser(USR_2)).thenReturn(false);
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(true);
        String response = "[ Student with username Gligor is not registered ]";
        assertEquals(response, cmdExecutor.executeCommand(POST_WISH_FOR_USR_2, channel));
    }

    @Test
    public void testNotAllowedPostWishCommandWhileNotLoggedIn() {
        when(storage.containsThisWishForUser(USR_2, WISH_1)).thenReturn(true);
        when(storage.isRegisteredUser(USR_2)).thenReturn(true);
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(false);
        String response = "[ You are not logged in ]";
        assertEquals(response, cmdExecutor.executeCommand(POST_WISH_FOR_USR_2, channel));
    }

    @Test
    public void testNotAllowedGetWishCommandWhileNotLoggedIn() {
        when(storage.containsThisWishForUser(USR_2, WISH_1)).thenReturn(true);
        when(storage.isRegisteredUser(USR_2)).thenReturn(true);
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(false);
        String response = "[ You are not logged in ]";
        assertEquals(response, cmdExecutor.executeCommand(GET_WISH, channel));
    }

    @Test
    public void testNotAllowedLogoutCommandWhileNotLoggedIn() {
        when(storage.containsThisWishForUser(USR_2, WISH_1)).thenReturn(true);
        when(storage.isRegisteredUser(USR_2)).thenReturn(true);
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(false);
        String response = "[ You are not logged in ]";
        String request = "logout";
        assertEquals(response, cmdExecutor.executeCommand(request, channel));
    }

    @Test
    public void testNotAllowedCommand() {
        String response = "[ Unknown command ]";
        String reply = "unknown";
        assertEquals(response, cmdExecutor.executeCommand(reply, channel));
    }

    @Test
    public void testDisconnectFromServer() {
        String response = "[ Disconnected from server ]";
        String reply = "disconnect";
        assertEquals(response, cmdExecutor.executeCommand(reply, channel));
    }

    @Test
    public void testLogin() {
        when(storage.isRegisteredUser(USR_2)).thenReturn(true);
        when(storage.getUserPassword(USR_2)).thenReturn("asdf");
        String request = "login Gligor asdf";
        String response = "[ User Gligor successfully logged in ]";
        assertEquals(response, cmdExecutor.executeCommand(request, channel));
    }

    @Test
    public void testLoginWithInvalidUsernamePasswordCombination() {
        when(storage.isRegisteredUser(USR_2)).thenReturn(true);
        when(storage.getUserPassword(USR_2)).thenReturn("asdg");
        String request = "login Gligor asdf";
        String response = "[ Invalid username/password combination ]";
        assertEquals(response, cmdExecutor.executeCommand(request, channel));
    }

    @Test
    public void testLogout() {
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(true);
        String request = "logout";
        String response = "[ Successfully logged out ]";
        assertEquals(response, cmdExecutor.executeCommand(request, channel));
    }

    @Test
    public void testGetWishReturnsWishes() {
        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(true);
        when(storage.getNumberOfStudentsWithWishes()).thenReturn(2);
        when(storage.isThereAWishForUser(USR_1)).thenReturn(true);
        when(storage.getLoggedInUserOnThisChannel(channel)).thenReturn(USR_1);
        Set<String> usr1Wishes = new HashSet<>();
        usr1Wishes.add("kolelo");
        usr1Wishes.add("telefon");
        Set<String> usr2Wishes = new HashSet<>();
        usr2Wishes.add("test");
        usr2Wishes.add("testing");
        Map<String, Set<String>> wishes = new HashMap<>();
        wishes.put(USR_1, usr1Wishes);
        wishes.put(USR_2, usr2Wishes);
        when(storage.getAllWishes()).thenReturn(wishes);
        when(storage.getWishesForUser(USR_2)).thenReturn(usr2Wishes);
        String request = "get-wish";
        String response = "[ Gligor: [test, testing] ]";
        assertEquals(response, cmdExecutor.executeCommand(request, channel));
    }

    @Test
    public void testGetWishReturnsNoUsersInWishlist() {

        when(storage.isLoggedInUserOnThisChannel(channel)).thenReturn(true);
        when(storage.getNumberOfStudentsWithWishes()).thenReturn(1);
        when(storage.isThereAWishForUser(USR_1)).thenReturn(true);
        when(storage.getLoggedInUserOnThisChannel(channel)).thenReturn(USR_1);
        Set<String> usr1Wishes = new HashSet<>();
        usr1Wishes.add("kolelo");
        usr1Wishes.add("telefon");
        Set<String> usr2Wishes = new HashSet<>();
        usr2Wishes.add("test");
        usr2Wishes.add("testing");
        Map<String, Set<String>> wishes = new HashMap<>();
        wishes.put(USR_1, usr1Wishes);
        wishes.put(USR_2, usr2Wishes);
        when(storage.getAllWishes()).thenReturn(wishes);
        when(storage.getWishesForUser(USR_2)).thenReturn(usr2Wishes);
        String request = "get-wish";
        String response = "[ There are no students present in the wish list ]";
        assertEquals(response, cmdExecutor.executeCommand(request, channel));
    }
}
