package exceptions;

public class InvalidQueryException extends InvalidRequestSyntaxException {
    public InvalidQueryException(String error) {
        super(error, true);
    }
}
