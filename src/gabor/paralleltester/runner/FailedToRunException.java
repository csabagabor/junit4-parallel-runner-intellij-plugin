package gabor.paralleltester.runner;

public class FailedToRunException extends RuntimeException {
    public FailedToRunException() {
    }

    public FailedToRunException(String message) {
        super(message);
    }

    public FailedToRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToRunException(Throwable cause) {
        super(cause);
    }
}
