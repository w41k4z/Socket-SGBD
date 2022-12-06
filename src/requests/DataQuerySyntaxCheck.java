package requests;

import java.util.ArrayList;
import java.util.Arrays;

import app.NialaSQL;
import databaseObjects.Database;
import databaseObjects.Relation;
import exceptions.ImpossibleOperationException;
import exceptions.InvalidOperatorException;
import exceptions.InvalidRequestSyntaxException;
import exceptions.InvalidTargetException;
import exceptions.MissingSemicolonException;
import exceptions.NoDatabaseSelectedException;
import interfaces.SyntaxCheck;
import utilities.RelationOperation;

public class DataQuerySyntaxCheck implements SyntaxCheck {

  private static final ArrayList<String> queries = new ArrayList<>(
    Arrays.asList("SHOW", "GET")
  );
  private static final ArrayList<String> relationSeparators = new ArrayList<>(
    Arrays.asList("UNION", "MINUS","INTERSECTION", "DIVIDE_BY")
  );

  public static boolean isDQS(String req) {
    return queries.contains(req.split(" ")[0].toUpperCase());
  }

  public void checkRequest(String request) throws Exception {
    if (request.split(" ").length < 3) throw new InvalidRequestSyntaxException(
      request.replace(";", "")
    );
    if (
      request.charAt(request.length() - 1) != ';'
    ) throw new MissingSemicolonException();
    this.checkTarget(request.replace(";", ""));
    this.checkSeparator(request.replace(";", ""));
  }

  public void checkTarget(String request) throws Exception {
    String[] reqs = request.split(" ");
    switch (reqs[0].toUpperCase()) {
      case "SHOW":
        if (
          !reqs[1].equalsIgnoreCase("ALL")
        ) throw new InvalidTargetException(
          "Invalid target. Do you mean \"SHOW ALL ...\" ?"
        );
        break;
      case "GET":
        if (
        !reqs[2].equalsIgnoreCase("FROM") 
        ) throw new InvalidRequestSyntaxException(request);
        if(reqs.length < 4) throw new InvalidRequestSyntaxException(request);
        if(reqs[3].length() == 0 || relationSeparators.contains(reqs[3].toUpperCase())) throw new InvalidRequestSyntaxException(request);
        break;
    }
  }

  public void checkSeparator(String request) throws Exception {
    String[] reqs = request.split(" ");
    if(reqs.length > 7) {
      int index = reqs[4].equalsIgnoreCase("WHERE") ? 6 : 4;
      String separator = reqs[index];
      if(!reqs[index + 1].equalsIgnoreCase("GET")) throw new ImpossibleOperationException();
      if(!relationSeparators.contains(separator.toUpperCase())) throw new InvalidOperatorException(separator);
      String splitter = reqs[0].concat(" ");
      for(int i = 1; i <= index; i++) {
        splitter = splitter.concat(reqs[i].concat(" "));
      }
      // to recurse so that all request are checked
      this.checkTarget(request.split(splitter)[1]);
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
        case "SHOW":
          switch (reqs[2].toUpperCase()) {
            case "DATABASE":
              result = NialaSQL.showAllDatabases();
              break;
            case "RELATION":
              // NoDatabaseSelectedException expected
              result = NialaSQL.showAllRelations(database[0]);
          }
          break;
        case "GET":
          if(database[0] == null) throw new NoDatabaseSelectedException();
          
          ArrayList<String> separators = getSeparator(reqs);
          // dividing the request by separators as : UNION, MINUS ...
          String[] requests = getRequests(request, separators);
          Relation newRelation = getRelationByRequest(requests[0], database[0]);
          
          int j = 1;
          for(int i = 0; i < separators.size(); i++) {
            switch(separators.get(i).toUpperCase()) {
              case "UNION":
                newRelation = RelationOperation.Union(newRelation, getRelationByRequest(requests[j], database[0]));
                break;
              case "MINUS":
                newRelation = RelationOperation.Difference(newRelation, getRelationByRequest(requests[j], database[0]));
                break;
              case "INTERSECTION":
                newRelation = RelationOperation.Intersection(newRelation, getRelationByRequest(requests[j], database[0]));
                break;
              case "DIVIDE_BY":
                newRelation = RelationOperation.Division(newRelation, getRelationByRequest(requests[j], database[0]));
                break;
            }
            j++;
          } 
          

          // if no exception, then
          result = newRelation.toString();
          break;
      }
    } catch (Exception exception) {
      result = exception.getMessage();
    }
    return result;
  }


  public static ArrayList<String> getSeparator(String[] reqs) {
    ArrayList<String> separators = new ArrayList<>();
    for(int i = 0; i < reqs.length; i++) {
      if(relationSeparators.contains(reqs[i].toUpperCase())) {
        separators.add(reqs[i]);
      }
    } 
    return separators;
  }

  // has to be called after a checkRequest to avoid invalidRequest
  public static String[] getRequests(String originRequest, ArrayList<String> separators) { // sorted separators 
    if(separators.size() == 0) return new String[] { originRequest };
    String[] requests = new String[separators.size() + 1];
    
    // to move on each request without duplicating already-passed request 
    String newReq = originRequest;
    int j = 0;
    for(int i = 0; i < separators.size(); i++) {
      String[] splittedReq = newReq.split(" " + separators.get(i) + " ", 2);
      requests[j] = splittedReq[0];
      j++;
      if(i == separators.size() - 1) requests[j] = splittedReq[1];
      newReq = splittedReq[1];
    }
    return requests;
  }

  // has to be called after a checkRequest to avoid invalidRequest
  public static Relation getRelationByRequest(String request, Database database) throws Exception {
    String[] reqs = request.split(" ");
    // getting the column to select-project
    String[] column = reqs[1].toUpperCase().equals("ALL")
      ? null
      : reqs[1].split(",");
    
    String spec = null;
    // checking WHERE condition
    if(reqs.length > 4) {
      if(reqs[4].equalsIgnoreCase("WHERE") && request.split(reqs[4] + " ").length > 1) {
          spec = request.split(reqs[4] + " ")[1];
          if(spec.length() == 0) throw new InvalidRequestSyntaxException(request.replace(";", ""));
      } else throw new InvalidRequestSyntaxException(request.replace(";", ""));
    }

    // getting the default relation from the database
    // possible NoSuchObjectException here
    Relation defaultRelation = database.getRelationByName(reqs[3]);
    return RelationOperation.Selection(defaultRelation, column == null ? defaultRelation.getColumns() : column, spec);
  }
}