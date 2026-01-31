package backend.satellite.exception;

public class TleDataNotFoundException extends RuntimeException {
    
    public TleDataNotFoundException(String message) {
        super(message);
    }
    
    public TleDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
