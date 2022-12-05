import java.util.*;

public class Factor implements Comparable<Factor> {
    /**
     * The Factor Class will hold six variables:
     * 1) _ID: The ID of the factor (i.e. f4(E))
     * 2) _var: The variable of the Factor.
     * 3) _table: A copy of the CPT table containing only the wanted rows for the factor.
     * 4) _row_size: The amount of rows in the _table.
     * 5) _dc: DataCleaner.
     * 6) _ascii_value.
     */
//    public String id;
    public ArrayList<String> _ID;
    public Variable _var;
    public ArrayList<HashMap<String, String>> _table;
    public int _row_size;
    public DataCleaner _dc;
    public int _ascii_value;

    /**
     * Empty Constructor for Factor:
     */
    public Factor(DataCleaner dc) {
        _dc = dc;
        _var = null;
        _table = new ArrayList<>();
        _row_size = 0;
        _ID = new ArrayList<>();
        _ascii_value = 0;
    }

    /**
     * Constructor for Factor.
     *
     * @param dc - The DataCleaner.
     */
    public Factor(Variable var, DataCleaner dc) {
        _var = new Variable(var);
        _table = new ArrayList<>();
        _dc = dc;
        _ID = new ArrayList<>();

        //Looping through the var CPT and adding relevant rows according to the evidence input:
        next_line:
        for (HashMap<String, String> cpt_line : _var.getCPT()._cpt_table) {
            for (int i = 0; i < dc._evidenceList.size(); i++) {
                if (cpt_line.containsKey(dc._evidenceList.get(i))) {
                    if (!cpt_line.get(dc._evidenceList.get(i)).equals(dc._evidenceValList.get(i)))
                        continue next_line;
                }
            }
            HashMap<String, String> line = new HashMap<>();
            for (Map.Entry<String, String> keyAndVal : cpt_line.entrySet()) {
                if (!dc._evidenceList.contains(keyAndVal.getKey()))
                    line.put(keyAndVal.getKey(), keyAndVal.getValue());
            }
            _table.add(line);
        }
        //Updating the row size:
        _row_size = _table.size();

        //calculating _ID:
        calcID();
        //Calculating the ASCII value:
        calcASCII();
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
        else if (_ascii_value > other._ascii_value) return 1;
        else if (other._ascii_value > _ascii_value) return -1;
        //If reached here, Factors are the same. Return current Factor:
        return 1;
    }

    /**
     * This function calculates the _ID ArrayList.
     */
    public void calcID() {
        _ID.clear();
        for (String variable : _table.get(0).keySet()) {
            if (!variable.equals("Prob"))
                _ID.add(variable);
        }
        Collections.sort(_ID);
    }

    /**
     * This function finds the ASCII value of the Factor.
     */
    public void calcASCII() {
        int ascii = 0;
        for (String str : _ID) {
            for (int i = 0; i < str.length(); i++) {
                ascii += str.charAt(i);
            }
        }
        _ascii_value = ascii;
    }

    /**
     * This function adds a new row to the table (used in VE).
     *
     * @param prob - the prbability to be added in the row.
     */
    public void add_row(String prob) {
        HashMap<String, String> new_row = new HashMap<>();
        new_row.put("Prob", prob);
        _table.add(new_row);
        _row_size++;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Factor _id: " + _ID + "\nRow Size: " + _row_size + "\nASCII Value: " + _ascii_value + "\n");
        for (HashMap<String, String> row : _table) {
            str.append(row.toString()).append("\n");
        }
        return str.toString();
    }
}