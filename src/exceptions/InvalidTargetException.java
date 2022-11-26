package exceptions;

public class InvalidTargetException extends InvalidRequestSyntaxException {
    public InvalidTargetException(String error) {
        super(error, true);
    }
}