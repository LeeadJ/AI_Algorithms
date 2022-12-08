import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Variable_Elimination_ABC_Order {
    /**
     * The Variable_Elimination_ABC_Order Class will hold seven variables:
     * 1) _dc: A DataCleaner reference.
     * 2) _variableList: A list of the query varibales.
     * 3) _answer: The final answer line (String)
     * 4) _multiplyCounter: A counter for the multiplication steps.
     * 5) _additionCounter: A counter for the addition steps.
     * 6) _factorList: A list of Factors according to the query.
     * 7) _eliminationOrderList: A list of variables representing the order of hidden variables to be eliminated.
     */

    public DataCleaner _dc;
    public ArrayList<Variable> _variableList;
    public String _answer;
    public ArrayList<Factor> _factorList;
    public ArrayList<String> _eliminationOrderList;
    public int _mulCounter = 0;
    public int _addCounter = 0;

    /**
     * Constructor for the Variable_Elimination_ABC_Order class.
     */
    public Variable_Elimination_ABC_Order(DataCleaner dc) {
        _dc = dc;
        _variableList = _dc._variableList;
        _answer = "";

        //Creating the factor list with the constraints:
        _factorList = new ArrayList<>();
        cleanFactorList();

        //Setting the hidden variable elimination by ABC order:
        _eliminationOrderList = new ArrayList<>(_dc._hiddenList);
        Collections.sort(_eliminationOrderList);
        System.out.println("Elimination order List: " + _eliminationOrderList + " size=" + _eliminationOrderList.size());
        System.out.print("Factor List: [  ");
        for (Factor f : _factorList)
            System.out.print(f._ID + "-" + f._row_size + "  ");
        System.out.println("]"); // 3 2 2 3 2
//        for(Factor f : _factorList)
//            System.out.println(f);

        //initializing the answer of the query:
        calculateQuery();
        System.out.println("Answer: " + _answer);

        //Prints
//        System.out.println("\n\t\tFactors");
//        System.out.println("Factor list size: " + _factorList.size());
//        System.out.println("_eliminationOrderList: " + _eliminationOrderList + "\n");
//        for (Factor f : _factorList)
//            System.out.println(f);
    }


    private void calculateQuery() {
        if (!resultGiven()) {
            //Looping over the variables to be joined and eliminated:
            for (String h_var : _eliminationOrderList) {
//            System.out.println("\n\n------------------------CHANGED-VAR------------------------");
                while (true) {
//                System.out.println("\n\n\t\tCURRENT VAR TO ELIMINATE: " + h_var);

                    //creating a list holding all the indexes from the _factorList of the Factors containing the variable:
                    ArrayList<Integer> h_varFactorList = new ArrayList<>();
                    for (int i = 0; i < _factorList.size(); i++) {
                        if (_factorList.get(i)._ID.contains(h_var))
                            h_varFactorList.add(i);
                    }
//                System.out.println("\n\t\tCURRENT h_varFactorList INDEX: "+h_varFactorList);
//                System.out.print("\n\t\tCURRENT h_varFactorList ID: ");

                    //If there are less than 2 factors containing the h_var, can't use join.
                    if (h_varFactorList.size() >= 2) {
                        //If reached here there are more than two Factors containing the h_var:
                        //Join the two first Factors (already ordered):
                        join(_factorList.get(h_varFactorList.get(0)), _factorList.get(h_varFactorList.get(1)));
                        continue;
                    } else if (h_varFactorList.size() > 0) {
                        //When reached here, the h_varFactorList contains less than two factors (single factor).
                        //Now we can eliminate the factor.
                        eliminate(_factorList.get(h_varFactorList.get(0)), h_var);
                    }
                    break;
                }
            }
//        System.out.println("\n\n--------------------------------FINISHED-----------------------");
//        System.out.println("\n\n\t\tFINAL FACTOR LIST: \n"+_factorList);

            //When reached here, all hidden variables were eliminated.
            //Keep joining the remaining factors holding the same variable:
            while (_factorList.size() > 1) {
                //Joining the remaining Factor (which hold the query variable):
                join(_factorList.get(0), _factorList.get(1));
            }

            //Normalizing the Factor:
            normalizeFactor(_factorList.get(0));
            for (HashMap<String, String> row : _factorList.get(0)._table)
                if (row.get(_dc._queryName).equals(_dc._queryVarValue)) {
                    _answer = row.get("Prob");
                    break;
                }
            _answer += "," + _addCounter + "," + _mulCounter;
        }
    }

    /**
     * This function checks if the result of the query is already in the variable CPT.
     */
    private boolean resultGiven() {
        boolean check1 = true;
        boolean check2 = false;
        next_row:
        for (HashMap<String, String> row : _dc._queryVariable.getCPT()._cpt_table) {
            if (check1) {
                for (int i = 0; i < _dc._evidenceList.size(); i++) {
                    if (!row.containsKey(_dc._evidenceList.get(i)))
                        return false;
                }
                check1 = false;
            }
            //checking if row contains query variable value and if the amount of ev variables equal the amount of ev variables in the row:
            if (row.get(_dc._queryName).equals(_dc._queryVarValue) && (_dc._evidenceList.size() + 2) == row.size()) {
                //looping over the variables and checking if row contains their values.
                for (int i = 0; i < _dc._evidenceList.size(); i++) {
                    //if row doesn't contain a value, go to next row:
                    if (!row.get(_dc._evidenceList.get(i)).equals(_dc._evidenceValList.get(i)))
                        continue next_row;
                }
                //If reached here, all values match up:
                double probability = Double.parseDouble(row.get("Prob"));
                NumberFormat format_5_digits = new DecimalFormat("#0.00000");
                String formatted_prob = format_5_digits.format(probability);
                _answer = formatted_prob + "," + _addCounter + "," + _mulCounter;
                check2 = true;
                break;
            }
        }
        return check2;
    }

    /**
     * This is the Join function.
     *
     * @param f1 - First factor to join.
     * @param f2 - Second Factor to join.
     *           Explanation:
     *           The function receives two Factors to join. The function finds the common variables between them.
     *           If a row contains the same value for all common variables between f2 and f2, the row will be merged (by multiplication).
     *           The resulting row will be entered in the new Factor.
     *           At the end, f1 and f2 will be removed from the _factorList and the new Factor will be added.
     */
    public void join(Factor f1, Factor f2) {
        Factor newF = new Factor(_dc);
        //creating an ArrayList that holds the common variables between f1 and f2:
        ArrayList<String> common_vars = new ArrayList<>();
        //Looping over the f1's first row of its table and checking the variables of the row:
        for (String key : f1._table.get(0).keySet()) {
            //checking if f1 and f2 share a common key (variable) except for the "Prob" key:
            if (f2._table.get(0).containsKey(key) && !key.equals("Prob"))
                common_vars.add(key);
        }

        //When reached here, List common_vars will hold the common variables between f1 and f2.
        for (int i = 0; i < f1._row_size; i++) {
            HashMap<String, String> f1_row = f1._table.get(i);
            for (int j = 0; j < f2._row_size; j++) {
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
                if (factorize) {
                    String probability = "" + Double.parseDouble((f1_row.get("Prob"))) * Double.parseDouble(f2_row.get("Prob"));
                    newF.add_row(probability);
                    _mulCounter++; //multiplied the factors line probability

                    //Adding the new variables from the join to newF if they are absent:
                    for (String k1 : f1_row.keySet())
                        newF._table.get(newF._row_size - 1).putIfAbsent(k1, f1_row.get(k1));
                    for (String k2 : f2_row.keySet())
                        newF._table.get(newF._row_size - 1).putIfAbsent(k2, f2_row.get(k2));
                }
            }
        }
        //Removing the old Facotrs f1 and f2:
//        System.out.println("\n\n\t\tJOINING FACTOR f1: "+f1._ID + " and " + f2._ID);
//        System.out.println("\n\t\tREMOVING FACTOR f1: "+f1);
        System.out.println("---first factor---\n" + f1);
        System.out.println("\n---second factor---\n" + f2);
        _factorList.remove(f1);
//        System.out.println("\t\tREMOVING FACTOR f2: "+f2+"\n\n");
        _factorList.remove(f2);
//        System.out.println("\t\tCURRENT FACTOR LIST: \n"+_factorList);

        //updating the newF fields:
        //_ID
        newF.calcID();
        //_ascii_value
        newF.calcASCII();
//        System.out.println("\n\n\t\tAdding FACTOR: "+newF);

        //Adding the newF to the factorList and resorting according to factor size:
        System.out.println("\n---new factor---\n" + newF);
        System.out.println("Addition: " + _addCounter + "------Mult: " + _mulCounter);
        _factorList.add(newF);
        _factorList.sort(Factor::compareTo);
//        System.out.println("\n\n\t\tCURRENT FACTOR LIST: \n"+_factorList);
    }

    /**
     * This is the Elimination function
     *
     * @param fact       - The Factor to work on.
     * @param hidden_var - The hidden variable to be eliminated.
     *                   Explanation:
     *                   The function creates a boolean array to indicate which rows of the Factor need to be deleted. This id done to avoid runtime errirs.
     *                   The function iterates over the rows of the given Factor in pairs.
     *                   If a pair contains all the normal variables and their values are the same, the rows will be merged (by addition).
     *                   Last, the marked rows (second row of each true pair) will be deleted from the given Factor and the result will be replaced in the first row.
     */
    public void eliminate(Factor fact, String hidden_var) {

//        System.out.println("\n\n\t\tELIMINATING VAR: " + hidden_var);
//        System.out.println("\n\n\t\tFACTOR TO ELIMINATE FROM: "+fact._ID);
//        System.out.println("\n\n\t\tFACTOR BEFORE ELIMINATION: \n"+fact);
        System.out.println("\nEliminate: " + fact._ID);
        //Creating n array indexing wich rows are to be deleted.
        Boolean[] rows_to_delete = new Boolean[fact._row_size];
        Arrays.fill(rows_to_delete, false);
        //Creating a list of the non-hidden variables, in order to know which rows to keep:
        ArrayList<String> normal_vars = new ArrayList<>();
        for (String var : fact._table.get(0).keySet()) {
            if (!var.equals(hidden_var) && !var.equals("Prob"))
                normal_vars.add(var);
        }
        //If the normal_var list is empty, then the Factor only holds its own values.
        //This means the Factor need to be dropped without any additional calculations:
        if (normal_vars.isEmpty()) {
            _factorList.remove(fact);
            System.out.println("\nFACTOR REMOVED FROM LIST\n");
            System.out.println("Addition: " + _addCounter + "------Mult: " + _mulCounter + "\n------------------------------");

            return;
        }
        for (int i = 0; i < fact._row_size; i++) {
            //If the index in rows_to_delete is true, skip:
            if (rows_to_delete[i])
                continue;
            //If reached here, the row is not to be deleted and needs to be checked if to be kept:
            HashMap<String, String> row1 = fact._table.get(i);
            for (int j = i + 1; j < fact._row_size; j++) {
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
                if (merge_rows) {
                    //multiplying the two probabilities and updating row1:
                    String probability = "" + (Double.parseDouble(row1.get("Prob")) + Double.parseDouble(row2.get("Prob")));
                    row1.replace("Prob", probability);
                    //updating row to delete:
                    rows_to_delete[j] = true;
                    _addCounter++;
                }
            }
            //removing the hidden key after it was eliminated:
            row1.remove(hidden_var);
        }
        //Here we need to remove all the marked true rows in the rows_to_delete array:
        int index_balancer = 0; // This will balance the indexes if previous ones were removed.
        for (int i = 0; i < rows_to_delete.length; i++) {
            if (rows_to_delete[i]) {
                fact._table.remove(i - index_balancer);
                fact._row_size--;
                index_balancer++;
            }
        }
        //Updating the Factor fields:
        fact.calcASCII();
        //Updating _ID:
        fact.calcID();
//        System.out.println("\n\n\t\tFACTOR AFTER ELIMINATION: \n"+fact);
        System.out.println("\nNew factor after join+eliminate: \n" + fact);
        System.out.println("Addition: " + _addCounter + "------Mult: " + _mulCounter + "\n------------------------------");
    }

    /**
     * This is the normalize function.
     *
     * @param fact - The Factor to normalize.
     *             Explanation:
     *             The function receives a Factor to normalize.
     *             It sums up all the probabilities in the rows of the Factor.
     *             Last, it divides the query variable value by the total probability sum and updates the row accordingly.
     */
    public void normalizeFactor(Factor fact) {
//        System.out.println("\n\n\t\t----------------NORMALIZING FACTOR--------------");

        double total_prob_sum = 0;
        //looping over the rows of the Factor and calculating the total probability sum:
        for (HashMap<String, String> row : fact._table) {
            total_prob_sum += Double.parseDouble(row.get("Prob"));
            _addCounter++;
        }
        _addCounter--;

//        add--; // subtracting from add because an extra add was taken for the first row.
//        System.out.println("\t\t\n\nTOTAL PROB SUM: "+total_prob_sum);

        //Calculating the normalized wanted query value probability:
        for (HashMap<String, String> row : fact._table) {
            if (row.get(_dc._queryName).equals(_dc._queryVarValue)) {
                double probability = (Double.parseDouble(row.get("Prob")) / total_prob_sum);
                NumberFormat format_5_digits = new DecimalFormat("#0.00000");
                String formatted_final_value = format_5_digits.format(probability);
                row.replace("Prob", formatted_final_value);
                break;
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
    private void cleanFactorList() {
        next_var:
        for (Variable var : _variableList) {
            if (_dc._evAndQMap.containsKey(var)) {
                Factor currF = new Factor(var, _dc);
                if (currF._row_size > 1) {
                    _factorList.add(currF);
                }
            } else {
                for (Variable evAndQVar : _dc._evAndQMap.keySet()) {
                    if (evAndQVar.isAncestor(var)) {
                        Factor currF = new Factor(var, _dc);
                        if (currF._row_size > 1) {
                            _factorList.add(new Factor(var, _dc));
                        }
                        continue next_var;
                    }
                }
            }
        }
        _factorList.sort(Factor::compareTo);
    }
}
