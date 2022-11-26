package exceptions;

public class InvalidRequestSyntaxException extends Exception {

  public InvalidRequestSyntaxException(String request) {
    super(
      "ERROR: \"" +
      request +
      "\" is not a valid request. Please, verify your request"
    );
  }

  public InvalidRequestSyntaxException(String message, boolean isSpecific) { // always true;
    super("ERROR: " + message);
  }
}
