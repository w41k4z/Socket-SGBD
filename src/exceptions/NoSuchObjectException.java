package exceptions;

public class NoSuchObjectException extends Exception {
    public NoSuchObjectException(Object object) {
        super("ERROR: This " + object.getClass().getSimpleName() + " does not exist");
    }
}
