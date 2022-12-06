// CREATE-DROP + exception(USE)
package requests;

import java.util.ArrayList;
import java.util.Arrays;

import databaseObjects.Database;
import databaseObjects.Relation;
import exceptions.InvalidRequestSyntaxException;
import exceptions.InvalidTargetException;
import exceptions.MissingSemicolonException;
import exceptions.UnknownDatabaseObjectException;
import interfaces.SyntaxCheck;

public class DataDefinitionSyntaxCheck implements SyntaxCheck {

  private static final ArrayList<String> queries = new ArrayList<>(
    Arrays.asList("USE", "CREATE", "DROP")
  );

  public static boolean isDDS(String req) {
    return queries.contains(req.split(" ")[0].toUpperCase());
  }

  public void checkRequest(String request) throws Exception {
    if (request.split(" ").length != 3) throw new InvalidRequestSyntaxException(
      request.replace(";", "")
    );
    if (
      request.charAt(request.length() - 1) != ';'
    ) throw new MissingSemicolonException();
    this.checkDatabaseObject(request.replace(";", ""));
  }

  public void checkDatabaseObject(String request) throws Exception {
    String[] reqs = request.split(" ");
    if (!SyntaxCheck.isDatabaseObject(reqs[1].toUpperCase())) {
      String errorMessage =
        reqs[1] + " is not a database Object. Try: DATABASE || RELATION ...";
      throw new UnknownDatabaseObjectException(errorMessage);
    }
  }

  public String executeRequest(String request, Database[] database) { // address != array
    String result = null;
    try {
      // InvalidRequestSyntaxException exception expected here
      this.checkRequest(request);

      // if no exception, then
      request = request.substring(0, request.length() - 1);
      String[] reqs = request.split(" ");

      switch (reqs[0].toUpperCase()) {
        case "USE": // USE DATABASE_OBJECT (Only for Database)
          if (reqs[1].toUpperCase().equalsIgnoreCase("RELATION")) {
            throw new InvalidTargetException(
              "Invalid target. Do you mean \"USE DATABASE " + reqs[1] + "\" ?"
            );
          }
          Database newDatabase = new Database(reqs[2]);

          // NoSuchObjectException exception expected here
          // affecting the value of the real database
          newDatabase.useDatabase(database);

          // if no exception, then
          result = "Connected to \"" + reqs[2] + "\"";

          break;
        case "CREATE": // CREATE DATABASE_OBJECT
          switch (reqs[1].toUpperCase()) {
            case "DATABASE":
              // DuplicationException exception expected here
              new Database(reqs[2]).createDatabase(); // creating and appending the new Database

              // if no exception, then
              result = "Database " + reqs[2] + " created successfully";

              break;
            case "RELATION":
              // InvalidRequestSyntaxException exception expected here
              Relation newRelation = new Relation(database[0], reqs[2], true);

              // NoDatabaseSelectedException || DuplicationException exception expected here
              newRelation.createRelation();

              // if no exception, then
              result = "Relation " + reqs[2] + " created successfully";
              break;
          }

          break;
        case "DROP": // DROP DATABASE_OBJECT
          switch (reqs[1].toUpperCase()) {
            case "DATABASE":
              // NoSuchObjectException exception expected here
              new Database(reqs[2]).dropDatabase();

              // if no exception, then
              // in case the user drop the current database
              if (
                database[0] != null &&
                reqs[2].equalsIgnoreCase(database[0].getName())
              ) {
                database[0] = null;
              }

              result = "DATABASE " + reqs[2] + " dropped successfully";

              break;
            case "RELATION":
              // NoDatabaseSelectedException || NoSuchObjectException exception expected here
              new Relation(database[0], reqs[2]).dropRelation();

              // if no exception, then
              result = "RELATION " + reqs[2] + " dropped successfully";

              break;
          }

          break;
      }
    } catch (Exception exception) {
      result = exception.getMessage();
    }
    return result;
  }
}
