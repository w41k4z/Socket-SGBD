package app;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import databaseObjects.Database;
import exceptions.NoDatabaseSelectedException;
import fileTreatments.ReadFile;
import interfaces.SyntaxCheck;
import requests.DataDefinitionSyntaxCheck;
import requests.DataManipulationSyntaxCheck;
import requests.DataQuerySyntaxCheck;

public class NialaSQL {

  // DDS : Data Definition Syntax (CREATE/DROP/USE ... ...)
  // DMS : Data Manipulation Syntax (ADD/DELETE/UPDATE ... ......)
  // DQL : Data Query Syntax (GET/SHOW ... ... [...])

  private Database[] database = new Database[1];

  public Database[] getDatabaseAddress() {
    return database;
  }

  public Database getDatabase() {
    return database[0];
  }

  public void getBanner() {
    System.out.print("\n\n");
    System.out.println(" ==================================");
    System.out.println(" ||                              ||");
    System.out.println(" || NialaSQL, the best SGBD ever ||");
    System.out.println(" ||                              ||");
    System.out.println(" ||         @Home made           ||");
    System.out.println(" ||                              ||");
    System.out.println(" ==================================");
    System.out.print("\n\n");
  }

  public void nialaSQLprompt() {
    System.out.print("NialaSQL> ");
  }

  public void nialaOutput(String out) {
    if (out != null) {
      System.out.println(out + "\n");
    }
  }

  // the app
  public void run(boolean reinitialize) {
    if (!reinitialize) this.getBanner();
    this.connectionToDatabase();
    this.runConsole();
  }

  public void end() {
    System.exit(0);
  }

  // DDRequest required when starting the app
  public void connectionToDatabase() {
    Scanner prompt = new Scanner(System.in);
    this.nialaSQLprompt();
    String request = prompt.nextLine();

    switch (request.toUpperCase()) {
      case "EXIT":
        this.end();
      default:
        this.executeNialaRequest(request, 0);
        break;
    }

    if (this.getDatabase() != null) {
      return;
    }
    this.connectionToDatabase();
  }

  public void runConsole() {
    if (this.getDatabase() == null) this.run(true); // in case the user drop the current database
    Scanner prompt = new Scanner(System.in);
    this.nialaSQLprompt();
    String request = prompt.nextLine();
    switch (request.toUpperCase()) {
      case "EXIT":
        this.end();
      default:
        this.executeNialaRequest(request, 1);
        break;
    }
    this.runConsole();
  }

  public void executeNialaRequest(String request, int phase) {
    SyntaxCheck syntaxCheck = null;
    if (DataDefinitionSyntaxCheck.isDDS(request)) {
      syntaxCheck = new DataDefinitionSyntaxCheck();
    } else if (DataQuerySyntaxCheck.isDQS(request)) {
      syntaxCheck = new DataQuerySyntaxCheck();
    } else if (DataManipulationSyntaxCheck.isDMS(request)) {
      syntaxCheck = new DataManipulationSyntaxCheck();
    } else {
      this.nialaOutput("ERROR: Undefined query");
      switch (phase) {
        case 0:
          this.connectionToDatabase();
          return;
        case 1:
          this.runConsole();
          return;
        default:
          return;
      }
    }

    // executing the request
    String result = syntaxCheck.executeRequest(
      request,
      this.getDatabaseAddress()
    );
    this.nialaOutput(result);
  }

  public static String showAllDatabases() throws Exception {
    ArrayList<String> databases = new ReadFile(
      new File("NialaSQLDatabases/Databases.db")
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
      new File("NialaSQLDatabases/" + database.getName() + "/Relations.rl")
    )
      .readFile(true);
    if (relations.size() == 0) return "0 RELATION detected";
    String result = "ALL RELATIONS detected from " + database.getName() + ":\n";
    for (int i = 0; i < relations.size(); i++) {
      result += "   " + (i + 1) + ") " + relations.get(i) + "\n";
    }
    return result;
  }

  public static void main(String[] args) throws Exception {
    NialaSQL nialaSQL = new NialaSQL();
    nialaSQL.run(false);
  }
}
