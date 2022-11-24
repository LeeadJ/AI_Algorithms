import java.util.ArrayList;
import java.util.HashMap;
public class CPT {
    /*The Cpt Class will hold two variables:
     * 1) The tables itself - the data structure will be an ArrayList of HashMaps.
     *     Each index represents a row of the CPT.
     * 2) row_size - for quick access to the row size.*/
    public ArrayList<HashMap<String, String>> _cpt_table;
    public int _row_size;

    /**Constructor for the CPT.*/
    public CPT(){
        this._cpt_table = new ArrayList<>();
        this._row_size = 0;
    }

    /**Copy Constructor for the CPT.
     * @param other - the other CPT to copy from.*/
    public CPT(CPT other){
        this._cpt_table = new ArrayList<>();
        this._cpt_table.addAll(other._cpt_table);
        this._row_size = other._row_size;
    }

    @Override
    public String toString(){
        String cpt = "\nRow Count: " + this._row_size + "\n";
        for(HashMap<String, String> map : this._cpt_table)
            cpt += map.toString() + "\n";
        return cpt;
    }





}
