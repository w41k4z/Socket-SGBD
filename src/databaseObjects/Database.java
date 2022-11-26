package databaseObjects;

import exceptions.NoSuchObjectException;
import fileTreatments.*;
import java.io.File;
import java.util.ArrayList;

public class Database extends DatabaseObject {

  private ArrayList<Relation> allRelation = new ArrayList<>();

  public Database(String name) {
    super(name);
  }

  private ArrayList<Relation> getAllRelation() {
    return this.allRelation;
  }

  public void appendRelation(Relation R) {
    this.getAllRelation().add(R);
  }

  public void removeRelation(Relation R) {
    for(int i = 0; i < this.getAllRelation().size(); i++) {
      if(this.getAllRelation().get(i).getName().equals(R.getName())) { 
        this.getAllRelation().remove(this.getAllRelation().get(i));
        return;
      } 
    }
  }

  public Relation getRelationByName(String name) throws NoSuchObjectException {
    for(int i = 0; i < this.getAllRelation().size(); i++) {
      if(this.getAllRelation().get(i).getName().equals(name)) return this.getAllRelation().get(i);
    }
    throw new NoSuchObjectException(new Relation());
  }

  public void checkRelationExistence(String name) throws NoSuchObjectException {
    for(int i = 0; i < this.getAllRelation().size(); i++) {
      if(this.getAllRelation().get(i).getName().equals(name)) return;
    }
    throw new NoSuchObjectException(new Relation());
  }

  public void createDatabase() throws Exception {
    File databaseStorage = new File("NialaSQLDatabases/Databases.db");

    this.checkDuplicates(databaseStorage);

    WriteFile writer = new WriteFile(databaseStorage);
    writer.writeFile(new String[] { this.getName() });

    File allDb = new File("NialaSQLDatabases/");
    File databaseDirectory = ExecuteFile.createNewFile(
      allDb,
      this.getName(),
      true
    );
    ExecuteFile.createNewFile(databaseDirectory, "Relations", true);
    ExecuteFile.createNewFile(databaseDirectory, "Relations.rl", false);
  }

  public void dropDatabase() throws Exception {
    this.checkExistence(new File("NialaSQLDatabases/Databases.db"));

    ReadFile reader = new ReadFile(new File("NialaSQLDatabases/Databases.db"));
    ArrayList<String> databases = reader.readFile(true);

    // deleting the directory
    ExecuteFile.deleteFile(new File("NialaSQLDatabases/" + this.getName()));

    // deleting the database from the file
    ExecuteFile.clearFile(new File("NialaSQLDatabases/Databases.db"));
    databases.remove(this.getName());

    // the method clear file really clear the file so here, it re-writes the rest
    new WriteFile(new File("NialaSQLDatabases/Databases.db"))
      .writeFile(databases.toArray());
  }

  public void useDatabase(Database[] toPassValue) throws Exception {
    this.checkExistence(new File("NialaSQLDatabases/Databases.db"));
    this.fetchRelation();
    toPassValue[0] = this;
  }

  public void fetchRelation() throws Exception {
    ArrayList<String> relationList = new ReadFile(new File("NialaSQLDatabases/" + this.getName() + "/Relations.rl")).readFile(true);
    for(int i = 0; i < relationList.size(); i++) {
      this.appendRelation(new Relation(this, relationList.get(i), null));
    }
  }
}
