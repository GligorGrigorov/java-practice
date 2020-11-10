package bg.sofia.uni.fmi.mjt.netflix.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found ex.");
    }
}
