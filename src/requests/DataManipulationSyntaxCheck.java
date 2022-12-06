package requests;

import java.util.ArrayList;
import java.util.Arrays;

import databaseObjects.Database;
import databaseObjects.Relation;
import exceptions.InvalidRequestSyntaxException;
import exceptions.MissingSemicolonException;
import interfaces.SyntaxCheck;

public class DataManipulationSyntaxCheck implements SyntaxCheck {

  private static final ArrayList<String> queries = new ArrayList<>(
    Arrays.asList("ADD", "UPDATE", "DELETE")
  );

  public static boolean isDMS(String request) {
    return queries.contains(request.split(" ")[0].toUpperCase());
  }

  public void checkRequest(String request) throws Exception {
    if (request.split(" ").length < 3) throw new InvalidRequestSyntaxException(
      request.replace(";", "")
    );
    if (
      request.charAt(request.length() - 1) != ';'
    ) throw new MissingSemicolonException();
    this.checkTarget(request.replace(";", ""));
  }

  public void checkTarget(String request) throws Exception {
    switch (request.split(" ")[0].toUpperCase()) {
      case "ADD":
        if (
          request.split(" ").length < 4 ||
          !request.split(" ")[1].equalsIgnoreCase("INTO") &&
          !request.split(" ")[3].toUpperCase().contains("VALUES")
        ) throw new InvalidRequestSyntaxException(request);
        break;
      case "UPDATE":
        if (
          !request.split(" ")[2].equalsIgnoreCase("SET")
        ) throw new InvalidRequestSyntaxException(request);
        break;
      case "DELETE":
        if (
          !request.split(" ")[1].equalsIgnoreCase("FROM")
        ) throw new InvalidRequestSyntaxException(request);
        break;
    }
  }

  public String executeRequest(String request, Database[] database) {
    String result = null;
    try {
      // InvalidRequestSyntaxException exception expected here
      this.checkRequest(request);

      request = request.substring(0, request.length() - 1);
      String[] reqs = request.split(" ");
      switch (reqs[0].toUpperCase()) {
        case "ADD":
          Relation toAppend = null;

          // A lot of exception expected here
          if (reqs[2].contains("<<") && reqs[2].contains(">>")) {
            // ex : add into Test<<id,description>> values<<1,helloWorld>>
            toAppend = new Relation(database[0], reqs[2], true);
          } else {
            // ex : add into Test values<<1,helloWorld,allDataCompleted>>
            toAppend = database[0].getRelationByName(reqs[2]);
          }

          // this is just to check the syntax for the values<<...>> and to throw an exception in case
          new Relation(null, reqs[3], true);

          // inserting data
          toAppend.insertData(reqs[3].split("<<")[1].split(">>")[0].split(","));
          result = "1 row inserted";

          break;
        case "UPDATE":
          Relation relation = database[0].getRelationByName(reqs[1]);
          relation.updateData(request.split("SET ")[1]);
          result = "Success";
          break;
      }
    } catch (Exception exception) {
      result = exception.getMessage();
    }
    return result;
  }
}
