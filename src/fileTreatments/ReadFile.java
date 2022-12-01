package fileTreatments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadFile extends BufferedReader {

  public ReadFile(File file) throws Exception {
    super(new FileReader(file));
  }

  public ArrayList<String> readFile(boolean toClose) throws Exception {
    ArrayList<String> data = new ArrayList<>();
    String dt = this.readLine();
    while (dt != null) {
      data.add(dt);
      dt = this.readLine();
    }
    if(toClose) this.close();

    return data;
  }

  public boolean contains(String string, boolean toClose) throws Exception {
    ArrayList<String> data = this.readFile(toClose);
    for (int i = 0; i < data.size(); i++) {
      if (data.get(i).contains(string)) {
        return true;
      }
    }
    return false;
  }
}