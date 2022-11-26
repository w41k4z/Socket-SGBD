package exceptions;

public class InvalidOperatorException extends Exception {
    public InvalidOperatorException(String operator) {
        super("ERROR: \"" + operator + "\" is not a valid operator");
    }
}
