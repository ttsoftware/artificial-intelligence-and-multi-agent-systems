package dtu.agency.services;

public class NoFreeNeighboursException extends RuntimeException {

    public NoFreeNeighboursException(String message) {
        super(message);
    }

    public NoFreeNeighboursException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoFreeNeighboursException(Throwable cause) {
        super(cause);
    }

    public NoFreeNeighboursException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
