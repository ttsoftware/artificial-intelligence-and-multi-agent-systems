package dtu.agency.services;

public class NotAFreeCellException extends RuntimeException {

    public NotAFreeCellException(String message) {
        super(message);
    }

    public NotAFreeCellException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAFreeCellException(Throwable cause) {
        super(cause);
    }

    public NotAFreeCellException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
