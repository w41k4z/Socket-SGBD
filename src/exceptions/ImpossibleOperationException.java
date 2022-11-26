package exceptions;

public class ImpossibleOperationException extends Exception {
  public ImpossibleOperationException() {
    super("ERROR: Impossible operation. Re-check your request");
  }
}