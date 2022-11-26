package databaseObjects;

import java.io.File;

import exceptions.DuplicationException;
import exceptions.NoSuchObjectException;
import fileTreatments.ReadFile;

public class DatabaseObject {

  String name;

  public DatabaseObject() {}

  public DatabaseObject(String name) {
    this.setName(name);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void checkExistence(File toSearch) throws Exception {
    ReadFile reader = new ReadFile(toSearch);
    if (!reader.contains(this.getName(), true)) throw new NoSuchObjectException(
      this
    );
  }

  public void checkDuplicates(File toSearch) throws Exception {
    ReadFile reader = new ReadFile(toSearch);
    if (reader.contains(this.getName(), true)) throw new DuplicationException(
      this,
      this.getName()
    );
  }
}
