package exceptions;

public class UnknownDatabaseObjectException extends InvalidRequestSyntaxException {
    public UnknownDatabaseObjectException(String theError) {
        super(theError, true);
    }
}
