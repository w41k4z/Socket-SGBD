package exceptions;

public class MissingSemicolonException extends InvalidRequestSyntaxException {
    public MissingSemicolonException() {
        super("Please, do not forget the semicolon at the end", true);
    }
}
