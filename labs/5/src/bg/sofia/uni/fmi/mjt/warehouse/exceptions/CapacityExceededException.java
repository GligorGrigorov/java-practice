package bg.sofia.uni.fmi.mjt.warehouse.exceptions;

public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(String message) {
        super(message);
    }
}
