import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Factor implements Comparable<Factor> {
    /** The Factor Class will hold four variables:
     * 1) _id: The ID of the factor (i.e. f4(E))
     * 2) _var: The variable of the Factor.
     * 3) _table: A copy of the CPT table containing only the wanted rows for the factor.
     * 4) _row_size: The amount of rows in the _table.*/
    public String _id;
    public Variable _var;
    public ArrayList<HashMap<String, String>> _table;
    public int _row_size;

    /** Empty Constructor for Factor: */
    public Factor(){
        _id = "";
        _var = null;
        _table = new ArrayList<>();
        _row_size = 0;
    }

    /** Constructor for Factor. */
    public Factor(Variable query_var, ArrayList<String> given_input){
        _var = new Variable(query_var);
        _id = _var.getName();
        _row_size = _var.getCPT()._row_size;
        _table = new ArrayList<>();

        //remove_rows = every true index of the array will be a row to remove according to the input and CPT
        Boolean[] remove_rows = new Boolean[_row_size+1];
        Arrays.fill(remove_rows, false);
        int rows_removed_counter = 0;

        //copying the original CPT table into the table variable of the class:
        _table.addAll(_var.getCPT()._cpt_table);

        //Looping through the  variable CPT and checking which rows to remove.
        for(int i=0; i<_var.getCPT()._row_size; i++) {
            for (int j = 0; j < given_input.size(); j++) {
                //creating a map for the current CPT table row:
                HashMap<String, String> rowMap = _var.getCPT()._cpt_table.get(i);
                //Splitting the row in order to extract the query variable.
                String[] queryList = given_input.get(i).split("=");
                if (rowMap.containsKey(queryList[0]) && !Objects.equals(rowMap.get(queryList[0]), queryList[1])){
                    remove_rows[i] = true;
                    break;
                }
            }
        }
        // Remove the rows from the table where they are false in the remove_rows array:
        for(int i=0; i<remove_rows.length; i++)
            if(remove_rows[i]){
                _table.remove(i-rows_removed_counter);
                rows_removed_counter++;
                _row_size--;
            }
    }

    /** This function allows us to compare a Factor size to another Factor.
     * @param other - Factor to compare to.
     * @return - 1: Current factor has more rows. -1: Given Factor has more rows.*/
    @Override
    public int compareTo(Factor other) {
        if(_row_size > other._row_size) return 1;
        else if(other._row_size > _row_size) return -1;
        //If the row sizes are the same, compare by ascii value:
        else if(this.asciiValue() > other.asciiValue()) return 1;
        else if(other.asciiValue() > this.asciiValue()) return -1;
        //If reached here, Factors are the same. Return current Factor:
        return 1;
    }

    @Override
    public String toString(){
      StringBuilder str = new StringBuilder("Factor _id: " + _id + "\nRow Size: " + _row_size + "\n");
        for (HashMap<String, String> row : _table) {
            str.append(row.toString()).append("\n");
        }
      return str.toString();
    }

    /** This function finds the ASCII value of the Factor.
     * @return - ascii value (int). */
    public int asciiValue(){
        int val = 0;
        for(String key : _table.get(0).keySet()){
            for(int i=0; i<key.length(); i++){
                val += key.charAt(i);
            }
        }
        return val;
    }

    /** This function adds a new row to the table (used in VE). */
    public void add_row(String prob){
        HashMap<String, String> new_row = new HashMap<>();
        new_row.put("Pr ", prob);
        _table.add(new_row);
        _row_size++;
    }
}
