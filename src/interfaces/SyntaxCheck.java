package interfaces;

import databaseObjects.Database;
import java.util.Arrays;
import java.util.ArrayList;

public interface SyntaxCheck {
  public void checkRequest(String request) throws Exception;

  public String executeRequest(String request, Database[] database);

  public static boolean isDatabaseObject(String theObject) {
    ArrayList<String> objects = new ArrayList<>(Arrays.asList("DATABASE", "RELATION"));
    return objects.contains(theObject);
  }

}
