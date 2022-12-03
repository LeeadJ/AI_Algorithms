import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Factor implements Comparable<Factor> {
    /**
     * The Factor Class will hold four variables:
     * 1) _id: The ID of the factor (i.e. f4(E))
     * 2) _var: The variable of the Factor.
     * 3) _table: A copy of the CPT table containing only the wanted rows for the factor.
     * 4) _row_size: The amount of rows in the _table.
     * 5) _dc: DataCleaner.
     */
    public String _id;
    public Variable _var;
    public ArrayList<HashMap<String, String>> _table;
    public int _row_size;
    public DataCleaner _dc;

    /**
     * Empty Constructor for Factor:
     */
    public Factor() {
        _id = "";
        _var = null;
        _table = new ArrayList<>();
        _row_size = 0;
        _dc = null;
    }

    /**
     * Constructor for Factor.
     * @param dc - The DataCleaner.
     */
    public Factor(Variable var, DataCleaner dc) {
        _var = new Variable(var);
        _id = _var.getName();
        _table = new ArrayList<>();
        this._dc = dc;
        //////////////////////////////////////
        //Looping through the var CPT and adding relevant rows according to the evidence input:
        next_line:
        for(HashMap<String, String> cpt_line : _var.getCPT()._cpt_table){
            for(int i=0; i<dc._evidenceList.size(); i++){
                if(cpt_line.containsKey(dc._evidenceList.get(i))){
                    if(!cpt_line.get(dc._evidenceList.get(i)).equals(dc._evidenceValList.get(i)))
                        continue next_line;
                }
            }
            _table.add(cpt_line);
        }
        _row_size = _table.size();
    }

    /**
     * This function allows us to compare a Factor size to another Factor.
     *
     * @param other - Factor to compare to.
     * @return - 1: Current factor has more rows. -1: Given Factor has more rows.
     */
    @Override
    public int compareTo(Factor other) {
        if (_row_size > other._row_size) return 1;
        else if (other._row_size > _row_size) return -1;
            //If the row sizes are the same, compare by ascii value:
        else if (this.asciiValue() > other.asciiValue()) return 1;
        else if (other.asciiValue() > this.asciiValue()) return -1;
        //If reached here, Factors are the same. Return current Factor:
        return 1;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Factor _id: " + _id + "\nRow Size: " + _row_size + "\n");
        for (HashMap<String, String> row : _table) {
            str.append(row.toString()).append("\n");
        }
        return str.toString();
    }

    /**
     * This function finds the ASCII value of the Factor.
     *
     * @return - ascii value (int).
     */
    public int asciiValue() {
        int val = 0;
        for (String key : _table.get(0).keySet()) {
            for (int i = 0; i < key.length(); i++) {
                val += key.charAt(i);
            }
        }
        return val;
    }

    /**
     * This function adds a new row to the table (used in VE).
     */
    public void add_row(String prob) {
        HashMap<String, String> new_row = new HashMap<>();
        new_row.put("Pr ", prob);
        _table.add(new_row);
        _row_size++;
    }
}
