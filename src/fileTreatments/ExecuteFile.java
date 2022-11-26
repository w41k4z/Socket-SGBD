package fileTreatments;

import java.io.File;
import java.io.FileOutputStream;

public class ExecuteFile {

  public static File createNewFile(
    File root,
    String fileName,
    boolean isDirectory
  ) throws Exception {
    File file = new File(root, fileName);
    if (isDirectory) {
      file.mkdir();
    } else {
      file.createNewFile();
    }
    return file;
  }

  public static void clearFile(File file) throws Exception {
    new FileOutputStream(file).close();
  }

  public static void deleteFile(File file) throws Exception {
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      for (int i = 0; i < children.length; i++) {
        deleteFile(children[i]);
      }
    }
    file.delete();
  }
}
