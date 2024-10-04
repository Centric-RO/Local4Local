package nl.centric.innovation.local4localEU.exception;

import java.io.Serial;

public class L4LEUException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public L4LEUException() {
        super();
    }

    public L4LEUException(String message) {
        super(message);
    }


    public L4LEUException(Throwable cause) {
        super(cause);
    }

    public L4LEUException(String message, Throwable cause) {
        super(message, cause);
    }

    public L4LEUException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
