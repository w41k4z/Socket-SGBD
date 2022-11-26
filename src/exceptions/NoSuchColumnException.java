package exceptions;

public class NoSuchColumnException extends Exception {

  public NoSuchColumnException(String[] column, String tableName) {
    super(
      "Error: Verify if " +
      String.join(",", column) +
      " really exist from the Relation " +
      tableName
    );
  }
}
