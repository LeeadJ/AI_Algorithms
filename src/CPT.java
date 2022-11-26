import java.util.ArrayList;
import java.util.HashMap;
public class CPT {
    /** The Cpt Class will hold two variables:
     * 1) _cpt_table: the data structure will be an ArrayList of HashMaps<String, String>.
     *     Each index represents a row of the CPT.
     * 2) _row_size: for quick access to the row size.*/
    public ArrayList<HashMap<String, String>> _cpt_table;
    public int _row_size;

    /**Constructor for the CPT.*/
    public CPT(){
        _cpt_table = new ArrayList<>();
        _row_size = 0;
    }

    /**Copy Constructor for the CPT.
     * @param other - the other CPT to copy from.*/
    public CPT(CPT other){
        _cpt_table = new ArrayList<>();
        _cpt_table.addAll(other._cpt_table);
        _row_size = other._row_size;
    }

    /** @return - String of the CPT. */
    @Override
    public String toString(){
        StringBuilder cpt = new StringBuilder("\nRow Count: " + _row_size + "\n");
        for(HashMap<String, String> map : _cpt_table)
            cpt.append(map.toString()).append("\n");
        return cpt.toString();
    }
}
