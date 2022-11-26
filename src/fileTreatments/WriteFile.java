package fileTreatments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class WriteFile extends BufferedWriter {

  public WriteFile(File file) throws Exception {
    super(new FileWriter(file, true));
  }

  public void writeFile(Object[] data) throws Exception {
    for (int i = 0; i < data.length; i++) {
      this.write(data[i].toString());
      this.newLine();
    }
    this.flush();
    this.close();
  }
}
