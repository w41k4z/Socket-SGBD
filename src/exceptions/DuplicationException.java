package exceptions;

public class DuplicationException extends Exception {

  public DuplicationException(Object duplicated, String objectName) {
    super(
      "ERROR: " +
      duplicated.getClass().getSimpleName() +
      " \"" +
      objectName +
      "\" already exists"
    );
  }
}
