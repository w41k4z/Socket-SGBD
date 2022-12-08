package databaseObjects;

import exceptions.InvalidOperatorException;
import exceptions.InvalidRequestSyntaxException;
import exceptions.NoDatabaseSelectedException;
import exceptions.NoSuchColumnException;
import fileTreatments.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Relation extends DatabaseObject {

  Database database;
  String[] columns;
  ArrayList<String[]> data = new ArrayList<>(); // by line

  public Relation() {}

  public Relation(Database database, String name, String[] columns)
    throws Exception {
    super(name);
    this.setDatabase(database);
    if (columns == null) {
      this.setDefaultColumns();
      this.fetchData();
    } else {
      this.setColumns(columns);
    }
  }

  // for dropping Relations
  public Relation(Database database, String name) {
    super(name);
    this.setDatabase(database);
  }

  // for DDS and DMS with a specified column
  public Relation(Database database, String syntax, boolean specific)
    throws InvalidRequestSyntaxException {
    super();
    if (specific) {
      if (syntax.contains(" ")) {
        throw new InvalidRequestSyntaxException(
          "This request contains some unauthorised white space.",
          true
        );
      } else if (
        syntax.startsWith("<<") ||
        !syntax.endsWith(">>") ||
        !syntax.contains("<<") ||
        syntax.split("<<")[0] == "" ||
        syntax.split("<<")[1].equals(">>")
      ) {
        throw new InvalidRequestSyntaxException(
          "Please, check the relation's columns.",
          true
        );
      }
      this.setDatabase(database);
      this.setName(syntax.split("<<")[0]);
      this.setColumns(syntax.split("<<")[1].split(">>")[0].split(","));
    }
  }

  // for operation like projection, passing data
  public Relation(Relation origin, String[] columns) throws Exception {
    // no database operation needed so
    this(null, origin.getName(), columns);
    ArrayList<String> originData = origin.toRawData();
    this.setData(this.extractData(originData));
  }

  public Relation(String name, String[] column,ArrayList<String[]> data) throws Exception {
    this(null, name);
    this.setColumns(column);
    this.setData(data);
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  public Database getDatabase() {
    return this.database;
  }

  public void setColumns(String[] col) {
    this.columns = col;
  }

  public String[] getColumns() {
    return this.columns;
  }

  public void setDefaultColumns() throws Exception {
    this.setColumns(this.getDefaultColumns());
  }

  public String[] getDefaultColumns() throws Exception {
    File relationAbout = new File(
      "/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" +
      this.getDatabase().getName() +
      "/Relations/" +
      this.getDatabase().getName() +
      "." +
      this.getName()
    );

    return this.decodeColumns(
        new ReadFile(relationAbout).readFile(true).get(0),
        "<==>"
    );
  }

  public void setData(ArrayList<String[]> data) {
    this.data = data;
  }

  public void appendData(String[] data) {
    this.data.add(data);
  }

  public ArrayList<String[]> getData() {
    return (ArrayList<String[]>) this.data.clone();
  }

  public ArrayList<String[]> selectData(String spec) throws Exception {
    if(spec == null) return this.getData();
    ArrayList<String[]> selected = new ArrayList<>();
    // spec like X=Y_AND_Z=A
    if(this.getData() != null) {
      // the spec split
      String[] conditions = spec.split("_");
      // operators like AND || OR
      String[] operator = conditions.length > 1 ? new String[conditions.length % 2] : null;
      // the specification like X=Y
      String[] specification = new String[(conditions.length/2)+1];
      // the column X
      String[] column = new String[specification.length];
      // the value Y
      String[] value = new String[specification.length];

      // extracting columns, values and operators
      for (int i = 0; i < specification.length; i++) {
        if(operator != null && i < operator.length) {
          operator[i] = conditions[i % 2 == 0 ? i + 1 : i];
        }
        specification[i] = conditions[i % 2 == 0 ? i : i + 1];
        column[i] = specification[i].split("=")[0];
        value[i] = specification[i].split("=")[1];
      }

      // checking if those conditions' column exist
      String[] currentColumn = this.getColumns();
      this.setColumns(column);
      this.checkColumns(currentColumn);

      // re-setting the default column
      this.setColumns(currentColumn);

      ArrayList<String> listCurrentColumn = new ArrayList<>(Arrays.asList(currentColumn));
      for (int i = 0; i < this.getData().size(); i++) {

        // first condition is the first specification
        boolean condition = this.getData().get(i)[listCurrentColumn.indexOf(column[0])].contains(value[0]);
        int k = 0;
        // structuring the condition
        for (int j = 1; j < specification.length && operator != null && k < operator.length; j++) {

          boolean nextCondition = this.getData().get(i)[listCurrentColumn.indexOf(column[j])].contains(value[j]);
          switch (operator[k].toUpperCase()) {
            case "AND":
              condition = condition && nextCondition;
              break;
            case "OR":
              condition = condition || nextCondition;
              break;
            default:
              throw new InvalidOperatorException(operator[k]);
          }
          k++;

        }

        if(condition == true) { selected.add(this.getData().get(i)); }

      }
    }
    return selected;
  }

  // from file
  public ArrayList<String[]> extractData(ArrayList<String> rawData)
    throws Exception {
    ArrayList<String[]> data = new ArrayList<>();
    // the first line of the raw data is always the default columns
    String[] defaultColumns = this.decodeColumns(rawData.get(0), "<==>");

    // to check if this relation columns matche the default columns from the rawData
    this.checkColumns(defaultColumns);

    ArrayList<String> defaultColumnAsArrayList = new ArrayList<>(
      Arrays.asList(defaultColumns)
    );

    String[] line;
    // browsing lines
    for (int i = 1; i < rawData.size(); i++) {
      line = this.decodeData(rawData.get(i), "<==>");
      String[] correspondingData = new String[this.getColumns().length];

      // matching data with the specified column (in case of projection)
      for (int j = 0; j < this.getColumns().length; j++) {
        // to get the corresponding data according to the specified column
        int dataIndex = defaultColumnAsArrayList.indexOf(this.getColumns()[j]);
        correspondingData[j] = line[dataIndex];
      }
      data.add(correspondingData);
    }
    return data;
  }

  public void fetchData() throws Exception {
    File relationAbout = new File(
      "/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" +
      this.getDatabase().getName() +
      "/Relations/" +
      this.getDatabase().getName() +
      "." +
      this.getName()
    );
    ArrayList<String> rawData = new ReadFile(relationAbout).readFile(true);
    this.setData(this.extractData(rawData));
  }

  public ArrayList<String> toRawData() {
    ArrayList<String> rawData = new ArrayList<>();
    rawData.add(this.encodeColumns("<==>"));
    for(int i = 0; i < this.getData().size(); i++) {
      rawData.add(this.encodeData(this.getData().get(i), "<==>"));
    }
    return rawData;
  }

  public void checkDatabase() throws NoDatabaseSelectedException {
    if (this.getDatabase() == null) throw new NoDatabaseSelectedException();
  }

  // another version of the superclass checkExistence
  public void checkExistence() throws Exception {
    this.checkDatabase();
    this.getDatabase().checkRelationExistence(this.getName());
  }

  // to verify if the actual columns matches the default columns passed in the parameter
  public void checkColumns(String[] defaultColumns)
    throws NoSuchColumnException {
    ArrayList<String> def = new ArrayList<>(Arrays.asList(defaultColumns));

    for (int i = 0; i < this.getColumns().length; i++) {
      if (def.indexOf(this.getColumns()[i]) == -1) {
        throw new NoSuchColumnException(this.getColumns(), this.getName());
      }
    }
  }

  // CRUD
  //============================================\\
  public void createRelation() throws Exception {
    this.checkDatabase();

    File relationStorage = new File(
      "/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" + this.getDatabase().getName() + "/Relations.rl"
    );
    this.checkDuplicates(relationStorage);

    File relationDirectory = new File(
      "/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" + this.getDatabase().getName() + "/Relations"
    );
    new WriteFile(relationStorage).writeFile(new String[] { this.getName() });

    // little convention, the first line of this new file must be this relation's columns
    File currentRelationFile = ExecuteFile.createNewFile(
      relationDirectory,
      this.getDatabase().getName() + "." + this.getName(),
      false
    );
    new WriteFile(currentRelationFile)
      .writeFile(new String[] { this.encodeColumns("<==>") });
    this.getDatabase().appendRelation(this);
  }

  public void dropRelation() throws Exception {
    this.checkExistence();

    // making a backup
    File relationStorage = new File(
      "/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" + this.getDatabase().getName() + "/Relations.rl"
    );
    ReadFile reader = new ReadFile(relationStorage);
    ArrayList<String> relations = reader.readFile(true);

    // deleting the relation's about
    ExecuteFile.clearFile(relationStorage);
    relations.remove(this.getName());
    ExecuteFile.deleteFile(
      new File(
        "/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" +
        this.getDatabase().getName() +
        "/Relations/" +
        this.getDatabase().getName() +
        "." +
        this.getName()
      )
    );
    this.getDatabase().removeRelation(this);

    // restoring backup
    new WriteFile(relationStorage).writeFile(relations.toArray());
  }

  //====================================================\\
  public void insertData(String[] data) throws Exception {
    this.checkExistence();

    String[] defaultColumns = this.getDefaultColumns();
    this.checkColumns(defaultColumns);
    String[] toInsert = new String[defaultColumns.length];

    if (this.getColumns().length != data.length) {
      throw new InvalidRequestSyntaxException(
        "The columns does not match the values",
        true
      );
    }

    // Arranging data according to the default column arrangement
    for (int i = 0; i < defaultColumns.length; i++) {
      boolean noValue = true;
      for (int j = 0; j < this.getColumns().length; j++) {
        if (defaultColumns[i].equalsIgnoreCase(this.getColumns()[j])) {
          noValue = false;
          toInsert[i] = data[j];
        }
      }
      if (noValue) toInsert[i] = "null";
    }

    File relationDirectory = new File(
      "/home/alain/Projects/JavaProject/Socket-SGBD/NialaSQLDatabases/" + this.getDatabase().getName() + "/Relations"
    );

    File currentRelationFile = ExecuteFile.createNewFile(
      relationDirectory,
      this.getDatabase().getName() + "." + this.getName(),
      false
    );
    new WriteFile(currentRelationFile)
      .writeFile(new String[] { this.encodeData(toInsert, "<==>") });
    this.getDatabase().getRelationByName(this.getName()).appendData(toInsert);
  }

  public void updateData(String valuesCondition) throws Exception { // col1=val1 WHERE condition
    String[] colVal = valuesCondition.split(" WHERE ")[0].split("=");
    String[] currentColumn = (String[]) this.getColumns().clone();
    
    String[] columnToChange = new String[colVal.length / 2];
    for(int i = 0; i < columnToChange.length; i ++) {
      columnToChange[i] = colVal[i == 0 ? 0 : i + 1];
    }
    
    // checking if the column to change value exist
    this.setColumns(columnToChange);
    this.checkColumns(currentColumn);
    // restoring the current column
    this.setColumns(currentColumn);

    for(int i = 0; i < this.getData().size(); i++) {
      for(int j = 0; j < columnToChange.length; j++) {
        // getting the index of the column to change
        int index = new ArrayList<>(Arrays.asList(currentColumn)).indexOf(columnToChange[j]);

        // we know that the colVal[x] = col and colVal[x + 1] = val 
        this.getData().get(i)[index] = colVal[j + 1];
      }
    }
  }

  //===========================================\\
  public String encodeColumns(String separator) {
    String coded = this.getColumns()[0];
    for (int i = 1; i < this.getColumns().length; i++) {
      coded += separator + this.getColumns()[i];
    }
    return coded;
  }

  public String[] decodeColumns(String col, String separator) {
    String[] strs = new String[col.split(separator).length];
    for (int i = 0; i < strs.length; i++) {
      strs[i] = col.split(separator)[i];
    }
    return strs;
  }

  public String encodeData(String[] data, String separator) {
    String coded = data[0];
    for (int i = 1; i < data.length; i++) {
      coded += separator + data[i];
    }
    return coded;
  }

  public String[] decodeData(String line, String separator) {
    String[] data = new String[line.split(separator).length];
    for (int i = 0; i < data.length; i++) {
      data[i] = line.split(separator)[i];
    }
    return data;
  }

  //=========================================\\
  public String toString() {
    String result = "";
    for (int i = 0; i < this.getColumns().length; i++) {
      result = result.concat(this.promptShow(" " + this.getColumns()[i]));
    }
    result += "\n";
    for (int j = 0; j < this.getData().size(); j++) {
      for (int k = 0; k < this.getData().get(j).length; k++) {
        result = result.concat(this.promptShow(" " + this.getData().get(j)[k]));
      }
      result += "\n";
    }
    return result;
  }

  public String promptShow(String toShow) {
    String result = "";
    int j = 0;
    for(int i = 0; i < 17; i++) {
      if(j < toShow.length()) {
        result = result.concat("" + toShow.charAt(j)); 
      } else {
        result = result.concat(" ");
      }
      j++;
    }
    return result.concat("|");
  }
}
