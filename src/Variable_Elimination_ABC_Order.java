import java.util.*;

public class Variable_Elimination_ABC_Order {
    /**
     * The Variable_Elimination_ABC_Order Class will hold ____ variables:
     * 1) _cpt_table: the data structure will be an ArrayList of HashMaps<String, String>.
     * Each index represents a row of the CPT. Each Hashmap
     * 2) _row_size: for quick access to the row size.
     */

    public DataCleaner _dc;
    public ArrayList<Variable> _variableList;
    public String _answer;
    public int _multiplyCounter = 0;
    public int _additionCounter = 0;
    public ArrayList<Factor> _factorList;
    public ArrayList<String> _eliminationOrderList;

    /** Constructor for the Variable_Elimination_ABC_Order class. */
    public Variable_Elimination_ABC_Order(DataCleaner dc) {
        _dc = dc;
        _variableList = _dc._variableList;
        System.out.println("\n\t\tFactors");
        _factorList = cleanFactorList();
        for (Factor f : _factorList)
            System.out.println(f);
        System.out.println("Factor list size: " + _factorList.size());


        //Setting the hidden variable elimination by ABC order:
        _eliminationOrderList = new ArrayList<>(_dc._hiddenList);
        Collections.sort(_eliminationOrderList);
        System.out.println("_eliminationOrderList: "+_eliminationOrderList);

        _answer = calculateQuery();
    }


    private String calculateQuery() {
        String ans = "sdsd";
        //Looping over the variables to be eliminated:
        for(String var_to_eliminate : _eliminationOrderList){
            //Creating a list of factors containing the var to be eliminated:
            ArrayList<Factor> factorsVarEliminateList = new ArrayList<>();
            for(Factor f : _factorList){
                if(f._ID.contains(var_to_eliminate)){
                    factorsVarEliminateList.add(f);
                    //removing the factor from the original list:
//                    _factorList.remove(f);
                }
            }
            factorsVarEliminateList.sort((f1, f2) -> Integer.compare(f1._row_size, f2._row_size));
            System.out.println("factorsVarEliminateList: "+factorsVarEliminateList);
//            Factor newFactor = joinAndEliminate(factorsVarEliminateList);
//            _factorList.add(newFactor);

        }
        return ans;
    }
    /** This is the Join function.
     * @param f1 - First factor to join.
     * @param f2 - Second Factor to join.
     * Explanation:
     * The function receives two Factors to join. The function finds the common variables between them.
     * If a row contains the same value for all common variables between f2 and f2, the row will be merged (by multiplication).
     * The resulting row will be entered in the new Factor.
     * At the end, f1 and f2 will be removed from the _factorList and the new Factor will be added.*/
    public void join(Factor f1, Factor f2){
        Factor newF = new Factor();
        //creating an ArrayList that holds the common variables between f1 and f2:
        ArrayList<String> common_vars = new ArrayList<>();
        //Looping over the f1's first row of its table and checking the variables of the row:
        for(String key : f1._table.get(0).keySet()){
            //checking if f1 and f2 share a common key (variable) except for the "Prob" key:
            if(f2._table.get(0).containsKey(key) && !key.equals("Prob"))
                common_vars.add(key);
        }
        //When reached here, List common_vars will hold the common variables between f1 and f2.

        for(int i=0; i<f1._row_size; i++){
            HashMap<String, String> f1_row = f1._table.get(i);
            for(int j=0; j<f2._row_size; j++){
                HashMap<String, String> f2_row = f2._table.get(j);
                boolean factorize = true;
                for (String common_var : common_vars) {
                    //Checking if the value of the common var is different in f1 and f2. If so, can't merge the lines.
                    if (!f1_row.get(common_var).equals(f2_row.get(common_var))) {
                        factorize = false;
                        break;
                    }
                }
                //If reached here and factorize is true, all common variables are the same. f1 and f2 lines can be merged.
                if(factorize){
                    String probability = ""+Double.parseDouble((f1_row.get("Prob"))) * Double.parseDouble(f2_row.get("Prob"));
                    newF.add_row(probability);
                    _multiplyCounter++;

                    //Adding the new variables from the join to newF if they are absent:
                    for(String k1 : f1_row.keySet())
                        newF._table.get(newF._row_size - 1).putIfAbsent(k1, f1_row.get(k1));
                    for(String k2 : f2_row.keySet())
                        newF._table.get(newF._row_size - 1).putIfAbsent(k2, f2_row.get(k2));
                }
            }
        }
        _factorList.remove(f1);
        _factorList.remove(f2);
        _factorList.add(newF);
        _factorList.sort(Factor::compareTo);
    }

    /** This is the Elimination function
     * @param fact - The Factor to work on.
     * @param hidden_var - The hidden variable to be eliminated.
     * Explanation:
     * The function creates a boolean array to indicate which rows of the Factor need to be deleted. This id done to avoid runtime errirs.
     * The function iterates over the rows of the given Factor in pairs.
     * If a pair contains all the normal variables and their values are the same, the rows will be merged (by addition).
     * Last, the marked rows (second row of each true pair) will be deleted from the given Factor and the result will be replaced in the first row.*/
    public void eliminate(Factor fact, String hidden_var){
        //Creating n array indexing wich rows are to be deleted.
        Boolean[] rows_to_delete = new Boolean[fact._row_size];
        Arrays.fill(rows_to_delete, false);
        //Creating a list of the non-hidden variables, in order to know which rows to keep:
        ArrayList<String> normal_vars = new ArrayList<>();
        for(String var : fact._table.get(0).keySet()){
            if(!var.equals(hidden_var) && !var.equals("Prob"))
                normal_vars.add(var);
        }

        for(int i=0; i<fact._row_size; i++){
            //If the index in rows_to_delete is true, skip:
            if(rows_to_delete[i])
                continue;
            //If reached here, the row is not to be deleted and needs to be checked if to be kept:
            HashMap<String, String> row1 = fact._table.get(i);
            for(int j=1; j<fact._row_size; j++){
                HashMap<String, String> row2 = fact._table.get(j);
                boolean merge_rows = true;
                //Looping over the variables from the normal_vars. Checking if the var value in both rows is the same.
                //If so, we need to merge the rows.
                for (String var : normal_vars) {
                    if (!row1.get(var).equals(row2.get(var))) {
                        merge_rows = false;
                        break;
                    }
                }
                //If reached here and merge_rows is true, the rows need to be merged:
                if(merge_rows){
                    //multiplying the two probabilities and updating row1:
                    String probability = "" + (Double.parseDouble(row1.get("Prob")) + Double.parseDouble(row2.get("Prob")));
                    row1.replace("Prob", probability);
                    //updating row to delete:
                    rows_to_delete[j] = true;
                    _additionCounter++;
                }
            }
            //removing the hidden key after it was eliminated:
            row1.remove(hidden_var);
        }
        //Here we need to remove all the marked true rows in the rows_to_delete array:
        int index_balancer = 0; // This will balance the indexes if previous ones were removed.
        for(int i=0; i<rows_to_delete.length; i++){
            if(rows_to_delete[i]){
                fact._table.remove(i - index_balancer);
                fact._row_size--;
                index_balancer++;
            }
        }
    }

    /**
     * This function creates a Factor list made up from the relevant Factors according to the query.
     * Criteria:
     * 1) The variable is the query variable.
     * 2) The variables is an evidence variable:
     * 3) The variable is an ancestor of the query or evidence variables.
     */
    private ArrayList<Factor> cleanFactorList() {
        ArrayList<Factor> factorList = new ArrayList<>();
        next_var:
        for (Variable var : _variableList) {
            if (_dc._evAndQMap.containsKey(var)) {
                Factor currF = new Factor(var, _dc);
                if (currF._row_size > 1) {
                    factorList.add(currF);
                }
            } else {
                for (Variable evAndQVar : _dc._evAndQMap.keySet()) {
                    if (evAndQVar.isAncestor(var)) {
                        Factor currF = new Factor(var, _dc);
                        if (currF._row_size > 1) {
                            factorList.add(new Factor(var, _dc));
                        }
                        continue next_var;
                    }
                }
            }
        }
        return factorList;
    }
}
