package comparators;

import java.util.Comparator;

public class DataComparator implements Comparator<String[]> {
    private int index;

    public DataComparator(int indexToCompare) {
        this.setIndex(indexToCompare);
    }

    private void setIndex(int index) { if(index > 0) this.index = index; }
    private int getIndex() { return this.index; }

    @Override
    public int compare(String[] data1, String[] data2) {
        return data1[this.getIndex()].compareTo(data2[this.getIndex()]);
    }
}
