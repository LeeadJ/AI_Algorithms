import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the DataCleaner class.
 * It will receive a query row, and clean the data according to certain variables.
 * It has ten variables:
 * 1) _queryVar: The query variable.
 * 2) _queryVarValue: The value of the query variable.
 * 3) _evidenceList: An Arraylist of type string representing the evidence variables.
 * 4) _evidenceValList: An Arraylist of type string representing the evidence variable values.
 * 5) _number: The number of the query representing the wanted algorithm
 * 6) _hiddenList - A list of the hidden variables.
 * 7) _outcome_combination_num - counts the amount of possible outcomes.
 * 8) _variableList - the query variable list in type variable.
 * 9) _permutationList - A list of the permutations needed.
 * 10) _evAndQMap - A Map of the query and evidence variables with key: variable and value: variable value.
 */
public class DataCleaner {
    //Query Example: P(B=T|J=T,M=T)
    public String _queryName; // (String) B
    public Variable _queryVariable; // (Variable) B
    public String _queryVarValue; // (String) T
    public ArrayList<String> _evidenceList; // [J, M]
    public ArrayList<String> _evidenceValList; // [T, T]
    public ArrayList<String> _evidenceVarValList; // [J=T, M=T]
    public char _number;
    public ArrayList<String> _hiddenList; // [A, E]
    public int _outcome_combination_num;
    public ArrayList<Variable> _variableList; // (Variable) [B, E, A, J, M]
    public ArrayList<ArrayList<String>> _permutationList;
    public HashMap<Variable, String> _evAndQMap; // (Map) {B=T, J=T, M=T}

    /**
     * This Constructor is in charge of cleaning the data.
     *
     * @param query - A single row from the input file.
     */
    public DataCleaner(String query, ArrayList<Variable> _vList) {
        _variableList = _vList;
        //Extracting the number from the query:
        _number = query.charAt(query.length() - 1);

        //Deleting unwanted letters from the query.
        query = query.substring(0, query.length() - 2);
        query = query.replace("P(", "");
        query = query.replace(")", "");

        //Extracting the rest of the parameters:
        _evidenceList = new ArrayList<>();
        _evidenceValList = new ArrayList<>();
        _evidenceVarValList = new ArrayList<>();
        String[] query_and_evidence_split = query.split("[|]");
        _queryName = query_and_evidence_split[0];
        String[] queryVar_and_value_split = _queryName.split("=");
        _queryName = _queryName.split("=")[0];
        _queryVarValue = queryVar_and_value_split[1];
        if (query_and_evidence_split.length > 1 && query_and_evidence_split[1].length() > 0) {
            String[] evidence = query_and_evidence_split[1].split(",");
            for (String index : evidence) {
                _evidenceVarValList.add(index);
                String[] index_values = index.split("=");
                _evidenceList.add(index_values[0]);
                _evidenceValList.add(index_values[1]);
            }
        }

        //initializing the _hiddenList:
        _hiddenList = new ArrayList<>();
        calcHidden();

        //calculating the number of outcome combinations:
        int num = 1;
        for (String name : _hiddenList)
            for (Variable var : _variableList)
                if (name.equals(var.getName()))
                    num *= var.getOutcomes().size();
        _outcome_combination_num = num;

        //finding the query Variable:
        for (Variable var : _variableList)
            if (var.getName().equals(_queryName))
                _queryVariable = var;

        //calculating all the wanted permutations:
        _permutationList = new ArrayList<>();
        calcPermutationList();

        //creating the evAndQList:
        _evAndQMap = new HashMap<>();
        _evAndQMap.put(_queryVariable, _queryVarValue);
        for (int i = 0; i < _evidenceList.size(); i++) {
            for (Variable var : _variableList) {
                if (var.getName().equals(_evidenceList.get(i))) {
                    _evAndQMap.put(var, _evidenceValList.get(i));
                    break;
                }
            }
        }
    }

    /**
     * This function loops over the variable list and updates the hiddenList according to the query.
     */
    public void calcHidden() {
        String[] arr = _queryName.split("=");
        for (Variable var : _variableList) {
            if (!_evidenceList.contains(var.getName()) && !var.getName().equals(arr[0]))
                _hiddenList.add(var.getName());
        }
    }

    /**
     * This function generates and updates a cleaner permutation list according to the wanted outcomes.
     */
    private void calcPermutationList() {
        ArrayList<String> outcomeList = new ArrayList<>();
        //loop through the hidden variables:
        for (String name : _hiddenList)
            //finding a match between hidden variable name and actual variable type:
            for (Variable var : _variableList)
                if (name.equals(var.getName())) {
                    //loop through the outcomes of the hidden variable:
                    for (String out : var.getOutcomes())
                        //check if outcomeList contains outcome. If not, add:
                        if (!outcomeList.contains(out))
                            outcomeList.add(out);
                    break;
                }

        int list_size = outcomeList.size();
        int perm_size = _hiddenList.size();
        outer:
        for (int i = 0; i < (int) Math.pow(list_size, perm_size); i++) {
            int index = i;
            ArrayList<String> alist = new ArrayList<>();
            for (int j = 0; j < perm_size; j++) {
                alist.add(outcomeList.get(index % list_size));
                index /= list_size;
            }
            //Checking if the alist is relevent to the wanted outcomes. If not, don;t add it to the flist.
            for (int j = 0; j < alist.size(); j++) {
                String temp_value = alist.get(j);
                String hidden_name = _hiddenList.get(j);
                //finding the variable:
                for (Variable var : _variableList) {
                    //matching hidden name with its variable type.
                    if (var.getName().equals(hidden_name)) {
                        //checking if the hidden variable contains the outcome at the index of the temp list:
                        if (!var.getOutcomes().contains(temp_value)) {
                            continue outer;
                        }
                        break;
                    }
                }
            }
            _permutationList.add(alist);
        }
    }
}