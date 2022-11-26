package exceptions;

public class NoDatabaseSelectedException extends Exception {

  public NoDatabaseSelectedException() {
    super("Error: No database selected yet");
  }
}
