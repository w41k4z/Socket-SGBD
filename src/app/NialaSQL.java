package app;

import java.io.File;

import java.util.ArrayList;

import java.io.PrintWriter;
import java.io.BufferedReader;

import databaseObjects.Database;

import exceptions.NoDatabaseSelectedException;

import fileTreatments.ReadFile;

import interfaces.SyntaxCheck;

import requests.DataDefinitionSyntaxCheck;
import requests.DataManipulationSyntaxCheck;
import requests.DataQuerySyntaxCheck;

public class NialaSQL {

  private boolean isConnected;

  // DDS : Data Definition Syntax (CREATE/DROP/USE ... ...)
  // DMS : Data Manipulation Syntax (ADD/DELETE/UPDATE ... ......)
  // DQL : Data Query Syntax (GET/SHOW ... ... [...])

  public void connect() {
    this.isConnected = true;
  }

  private Database[] database = new Database[1];

  public Database[] getDatabaseAddress() {
    return database;
  }

  public Database getDatabase() {
    return database[0];
  }

  public void getBanner(PrintWriter out) throws Exception {
    out.print("\n\n");
    out.println(" ==================================");
    out.println(" ||                              ||");
    out.println(" || NialaSQL, the best SGBD ever ||");
    out.println(" ||                              ||");
    out.println(" ||         @Home made           ||");
    out.println(" ||                              ||");
    out.println(" ==================================");
    out.print("\n\n\n");
    out.flush();
  }

  public void nialaOutput(PrintWriter out, String output) throws Exception {
    if (output != null) {
      out.println(output + "\n");
      out.flush();
    }
  }

  // the app
  public void run(BufferedReader in, PrintWriter out, boolean reinitialize) throws Exception {
    if (this.isConnected) {
      if (!reinitialize) this.getBanner(out);
      this.connectionToDatabase(in , out);
      this.runConsole(in, out);
    }
  }

  // DDRequest required when starting the app
  public void connectionToDatabase(BufferedReader in, PrintWriter out) throws Exception {
    if (this.isConnected) {
      // reading the client input
      String request = in.readLine();
      
      switch (request.toUpperCase()) {
        case "EXIT":
          this.isConnected = false;
          return;
        default:
          this.executeNialaRequest(in, out, request, 0);
          break;
      }
  
      if (this.getDatabase() != null) {
        return;
      }
      this.connectionToDatabase(in, out);

    }
  }

  public void runConsole(BufferedReader in, PrintWriter out) throws Exception {
    if (this.isConnected) {
      
      if (this.getDatabase() == null)
        this.run(in, out, true); // in case the user drop the current database
  
      // reading the client input
      String request = in.readLine();

      switch (request.toUpperCase()) {
        case "EXIT":
          this.isConnected = false;
          return;
        default:
          this.executeNialaRequest(in, out, request, 1);
          break;
      }
      this.runConsole(in, out);

    }
  }

  public void executeNialaRequest(BufferedReader in, PrintWriter out, String request, int phase) throws Exception {
    SyntaxCheck syntaxCheck = null;
    if (DataDefinitionSyntaxCheck.isDDS(request)) {
      syntaxCheck = new DataDefinitionSyntaxCheck();
    } else if (DataQuerySyntaxCheck.isDQS(request)) {
      syntaxCheck = new DataQuerySyntaxCheck();
    } else if (DataManipulationSyntaxCheck.isDMS(request)) {
      syntaxCheck = new DataManipulationSyntaxCheck();
    } else {
      this.nialaOutput(out, "ERROR: Undefined query");
      switch (phase) {
        case 0:
          this.connectionToDatabase(in, out);
          return;
        case 1:
          this.runConsole(in, out);
          return;
      }
    }

    // executing the request
    String result = syntaxCheck.executeRequest(
      request,
      this.getDatabaseAddress()
    );
    this.nialaOutput(out, result);
  }

  public static String showAllDatabases() throws Exception {
    ArrayList<String> databases = new ReadFile(
      new File("/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/Databases.db")
    )
      .readFile(true);
    if (databases.size() == 0) return "0 DATABASE detected";
    String result = "ALL DATABASES detected:\n";
    for (int i = 0; i < databases.size(); i++) {
      result += "\t" + (i + 1) + ") " + databases.get(i) + "\n";
    }
    return result;
  }

  public static String showAllRelations(Database database) throws Exception {
    if (database == null) throw new NoDatabaseSelectedException();
    ArrayList<String> relations = new ReadFile(
      new File("/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" + database.getName() + "/Relations.rl")
    )
      .readFile(true);
    if (relations.size() == 0) return "0 RELATION detected";
    String result = "ALL RELATIONS detected from " + database.getName() + ":\n";
    for (int i = 0; i < relations.size(); i++) {
      result += "   " + (i + 1) + ") " + relations.get(i) + "\n";
    }
    return result;
  }
}