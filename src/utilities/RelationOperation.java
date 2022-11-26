package utilities;

import java.util.ArrayList;
import java.util.Arrays;

import comparators.DataComparator;
import databaseObjects.Relation;
import exceptions.ImpossibleOperationException;

public class RelationOperation {

  public static Relation Projection(Relation r, String[] columns) throws Exception {
    Relation newRelation = new Relation(r, columns); // r is the default relation from database
    deleteDuplicates(newRelation.getData());
    return  newRelation;
  }

  public static Relation Selection(Relation origin, String[] columns, String spec) throws Exception {
      ArrayList<String[]> selectedData = origin.selectData(spec);
      return Projection(new Relation(origin.getName(), origin.getColumns(), selectedData), columns);
  }

  public static Relation Union(Relation r1, Relation r2) throws Exception {
    if(r1.getColumns().length != r2.getColumns().length || !r1.getName().equals(r2.getName())) throw new ImpossibleOperationException();
    // making the two relation identical with column
    r2 = Projection(r2, r1.getColumns());

    ArrayList<String[]> r1Data = r1.getData();
    ArrayList<String[]> r2Data = r2.getData();

    // merging r2Data with r1Data
    mergeData(r1Data, r2Data);
    deleteDuplicates(r1Data);

    return new Relation(r1.getName(), r1.getColumns(), r1Data);
  }

  public static Relation Difference(Relation r1, Relation r2) throws Exception {
    if(r1.getColumns().length != r2.getColumns().length || !r1.getName().equals(r2.getName())) throw new ImpossibleOperationException();
    // making the two relation identical with column
    r2 = Projection(r2, r1.getColumns());

    ArrayList<String[]> r1Data = r1.getData();
    ArrayList<String[]> r2Data = r2.getData();

    // no merging so no possible duplicates
    removeFrom(r1Data, r2Data);

    return new Relation(r1.getName(), r1.getColumns(), r1Data);
  }

  public static Relation Intersection(Relation r1, Relation r2) throws Exception {
    if(r1.getColumns().length != r2.getColumns().length || !r1.getName().equals(r2.getName())) throw new ImpossibleOperationException();
    // making the two relation identical with column
    r2 = Projection(r2, r1.getColumns());

    ArrayList<String[]> r1Data = r1.getData();
    ArrayList<String[]> r2Data = r2.getData();

    // no merging so no possible duplicates
    return new Relation(r1.getName(), r1.getColumns(), getFrom(r1Data, r2Data));
  }
  
  public static Relation CartesianProduct(Relation r1, Relation r2) throws Exception {
    ArrayList<String[]> r1Data = r1.getData();
    ArrayList<String[]> r2Data = r2.getData();

    String[] developedColumn = String.join(",", r1.getColumns()).concat("," + String.join(",", r2.getColumns())).split(",");

    ArrayList<String[]> developed = develop(r1Data, r2Data);
    deleteDuplicates(developed);

    return new Relation(r1.getName(), developedColumn, developed);
  }

  public static Relation Division(Relation r1, Relation r2) throws Exception {
    if(r2.getColumns().length > r1.getColumns().length) throw new ImpossibleOperationException();
    
    ArrayList<String> r1Cols = new ArrayList<>(Arrays.asList(r1.getColumns()));
    for (int i = 0; i < r2.getColumns().length; i++) {
      if(!r1Cols.contains(r2.getColumns()[i])) throw new ImpossibleOperationException();
      r1Cols.remove(r2.getColumns()[i]);
    }
    String[] toProject = new String[r1.getColumns().length - r2.getColumns().length];
    for (int i = 0; i < r1Cols.size(); i++) {
      toProject[i] = r1Cols.get(i);
    }

    
    Relation operation1 = Projection(r1, toProject);
    Relation operation2 = CartesianProduct(operation1, r2);
    Relation operation3 = Difference(operation2, r1);
    Relation operation4 = Projection(operation3, toProject);
    return Difference(operation1, operation4);
  }


  //================================================================================\\
  // specific operation
  public static void mergeData(ArrayList<String[]> data1, ArrayList<String[]> data2) {
    for(int i = 0; i < data2.size(); i++) {
      data1.add(data2.get(i));
    }
  }

  public static void deleteDuplicates(ArrayList<String[]> toCheck) {
    // to make the operation faster
    toCheck.sort(new DataComparator(0));
    for (int i = 0; i < toCheck.size() - 1; i++) {
      if(Arrays.deepEquals(toCheck.get(i), toCheck.get(i + 1))) {
        toCheck.remove(i);
        i--;
      }
    }
  }

  public static void removeFrom(ArrayList<String[]> defaultArray, ArrayList<String[]> toRemove) {
    defaultArray.sort(new DataComparator(0));
    toRemove.sort(new DataComparator(0));
    int i = 0;
    for (int j = 0; j < defaultArray.size() && i < toRemove.size(); j++) {
      if(Arrays.deepEquals(toRemove.get(i), defaultArray.get(j))) {
        defaultArray.remove(j);
        j--;
        i++;
      }
    }
  }

  public static ArrayList<String[]> getFrom(ArrayList<String[]> defaultArray, ArrayList<String[]> toExtract) {
    ArrayList<String[]> intersect = new ArrayList<>();
    defaultArray.sort(new DataComparator(0));
    toExtract.sort(new DataComparator(0));
    int i = 0;
    for (int j = 0; j < defaultArray.size() && i < toExtract.size(); j++) {
      if (Arrays.deepEquals(toExtract.get(i), defaultArray.get(j))) {
        intersect.add(defaultArray.get(j));
        i++;
      }
    }
    return intersect;
  }
  
  public static ArrayList<String[]> develop(ArrayList<String[]> defaultArray, ArrayList<String[]> toDevelop) {
    ArrayList<String[]> developed = new ArrayList<>();
    for (int i = 0; i < defaultArray.size(); i++) {
      for (int j = 0; j < toDevelop.size(); j++) {
        String line = String.join(",", defaultArray.get(i)).concat("," + String.join(",", toDevelop.get(j)));
        developed.add(line.split(","));
      }
    }
    return developed;
  }
}