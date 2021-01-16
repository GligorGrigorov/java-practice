package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.storage.ServerStorage;
import bg.sofia.uni.fmi.mjt.wish.list.storage.Storage;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class ServerStorageTest {
    private Storage storage;
    private static final String USERNAME = "Name";
    private static final String PASSWORD = "Pass";
    private static final String WISH = "Wish";
    private SocketChannel channel;

    @Before
    public void setup() {
        try {
            channel = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        storage = new ServerStorage();
        storage.addRegisteredUser(USERNAME, PASSWORD);
        storage.addLoggedInUser(USERNAME, channel);
        storage.addWishForStudent(USERNAME, WISH);
    }

    @Test
    public void testIsLoggedInUserOnThisChannel() {
        assertTrue(storage.isLoggedInUserOnThisChannel(channel));
    }

    @Test
    public void testIsRegisteredUser() {
        assertTrue(storage.isRegisteredUser(USERNAME));
    }

    @Test
    public void testIsThereAWishForUser() {
        assertTrue(storage.isThereAWishForUser(USERNAME));
    }

    @Test
    public void testContainsThisWishForUser() {
        assertTrue(storage.containsThisWishForUser(USERNAME, WISH));
    }

    @Test
    public void testGetLoggedInUserOnThisChannel() {
        assertEquals(USERNAME, storage.getLoggedInUserOnThisChannel(channel));
    }

    @Test
    public void testGetNumberOfStudentsWithWishes() {
        assertEquals(1, storage.getNumberOfStudentsWithWishes());
    }

    @Test
    public void testGetAllWishes() {
        Map<String, Set<String>> wishes = storage.getAllWishes();
        assertTrue(wishes.containsKey(USERNAME));
        assertTrue(wishes.get(USERNAME).contains(WISH));
        assertEquals(1, wishes.get(USERNAME).size());
    }

    @Test
    public void testGetWishesForStudent() {
        assertTrue(storage.getWishesForUser(USERNAME).contains(WISH));
    }

    @Test
    public void testRemoveWishesForUser() {
        storage.removeWishesForUser(USERNAME);
        assertTrue(storage.getAllWishes().isEmpty());
    }

    @Test
    public void testRemoveLoggedInUser() {
        storage.removeLoggedInUser(channel);
        assertFalse(storage.isLoggedInUserOnThisChannel(channel));
    }
}
