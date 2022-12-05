import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class BayesRule {
    /**
     * The BayesRule Class will hold eleven variables:
     * 1) _variableList: A list of the query Variables given by the XML file.
     * 2) _dc: The DataCleaner reference.
     * 3) _answer: The final answer output.
     * 4) _multiplyCounter: A counter for the multiplication steps (precalculated).
     * 5) _additionCounter: A counter for the addition steps (precalculated).
     * 6) _permutations: A list of all the wanted permutations given the query variable and evidence.
     * 7) _fullVarList: A list containing the variables according the permutations list.
     * 8) _fullValueList: A list containing the variable Values according to the permutation list.
     * 9) _valueListByMap: A map of key: variable and value: variable value.
     * 10) _denominator: The denominator once the query equation is opened.
     * 11) _numerator: The numerator once the query equation is opened (complement + denominator).
     */
    public ArrayList<Variable> _variableList;
    public DataCleaner _dc;
    public String _answer;
    public int _multiplyCounter = 0;
    public int _additionCounter = 0;
    public ArrayList<ArrayList<String>> _permutations;
    public ArrayList<ArrayList<String>> _fullVarList;
    public ArrayList<ArrayList<String>> _fullValueList;
    public ArrayList<HashMap<String, String>> _valueListByMap;
    public double _denominator;
    public double _numerator;

    /**
     * Constructor for the BayesRule class.
     */
    public BayesRule(ArrayList<Variable> varList, DataCleaner dc) {
        _variableList = varList;
        _dc = dc;
        //Calculating addition and multiplication:
        calcMul();
        calcAdd();
        _permutations = _dc._permutationList;
        _fullValueList = new ArrayList<>();
        _fullVarList = new ArrayList<>();
        //updating the lists in according to the permutation list:
        for (ArrayList<String> temp : _permutations) {
            ArrayList<String> curr1 = new ArrayList<>();
            ArrayList<String> curr2 = new ArrayList<>();
            //adding the query Var and value;
            curr1.add(_dc._queryName);
            curr2.add(_dc._queryVarValue);
            //Adding the evidence var and value:
            curr1.addAll(_dc._evidenceList);
            curr2.addAll(_dc._evidenceValList);
            //Adding the hidden var and val:
            curr1.addAll(_dc._hiddenList);
            curr2.addAll(temp);
            //adding the curr lists to the variables:
            _fullVarList.add(curr1);
            _fullValueList.add(curr2);
        }
        _valueListByMap = new ArrayList<>();
        for (int i = 0; i < _fullVarList.size(); i++) {
            HashMap<String, String> temp = new HashMap<>();
            for (int j = 0; j < _fullVarList.get(i).size(); j++) {
                String var = _fullVarList.get(i).get(j);
                temp.put(var, _fullValueList.get(i).get(j));
            }
            _valueListByMap.add(temp);
        }
        //calculating the answer:
        calculateQuery();
    }

    /**
     * This function calculates the amount of multiplication steps needed to solve the query.
     * Logic: (num of variables -1) * (num of outcome combinations) * (size of outcome list) [-because of the normalization].
     */
    private void calcMul() {
        _multiplyCounter = (_variableList.size() - 1) * (_dc._outcome_combination_num) * (_dc._queryVariable.getOutcomes().size());
    }

    /**
     * This function calculates the amount of addition steps needed to solve the query.
     * Logic: (num of outcome combinations) * (size of outcome list) [-because of the normalization] + (1) [-because of the numerator]
     */
    private void calcAdd() {
        _additionCounter = (_dc._outcome_combination_num - 1) * (_dc._queryVariable.getOutcomes().size()) + (1);
    }

    /**
     * This function calculates the query and updates the _answer.
     */
    private void calculateQuery() {
        //calculating the denominator and numerator:
        _denominator = calcDenominator();
        _numerator = calcComplement() + _denominator;
        //calculating the probabilitiy:
        double probability = _denominator / _numerator;
        //formatting the probability to 5th decimal:
        NumberFormat format_5_digits = new DecimalFormat("#0.00000");
        String formatted_prob = format_5_digits.format(probability);
        //Updating _answer:
        _answer = formatted_prob + "," + _additionCounter + "," + _multiplyCounter;
    }

    /**
     * This function calculates the Denominator.
     * Explanation:
     * The function loops over all the variables and calculates the probabilities according to the permutation list and evidence.
     */
    public double calcDenominator() {
        //initiating the final answer variable:
        double final_ANSWER = 0;

        //Looping through the variable and value map according to the evidence and given query outcomes:
        for (HashMap<String, String> tempMap : _valueListByMap) {
            double tempMapSUM = 1;

            //looping through each variable of the query to find its probability.
            next_var:
            for (Variable currVar : _variableList) {
                //creating a map for all of its dependencies:
                HashMap<String, String> varDependencyMap = new HashMap<>();
                //inserting the current variables value:
                varDependencyMap.put(currVar.getName(), tempMap.get(currVar.getName()));
                //looping through the current variables parents and adding the dependencies:
                for (Variable parent : currVar.getParents()) {
                    varDependencyMap.put(parent.getName(), tempMap.get(parent.getName()));
                }

                //looping through the current variables CPT to find the correct line:
                cpt_line:
                for (HashMap<String, String> cpt_line_Map : currVar.getCPT()._cpt_table) {
                    for (Map.Entry<String, String> condVarKeyVal : varDependencyMap.entrySet()) {
                        //checking if the dependency keys hold different values. If so, go to next CPT line:
                        if (!condVarKeyVal.getValue().equals(cpt_line_Map.get(condVarKeyVal.getKey())))
                            continue cpt_line;
                    }
                    //If reached here, all condVarKeyVal key and values equal. FOUND CORRECT CPT LINE!
                    //Add the Probability to the total sum:
                    tempMapSUM *= Double.parseDouble(cpt_line_Map.get("Prob"));
                    continue next_var;
                }
            }
            //Adding the tempMap sum to the final answer:
            final_ANSWER += tempMapSUM;
        }
        return final_ANSWER;
    }

    /**
     * This function calculates the Numerator.
     * Explanation:
     * The function loops through the different outcomes of the query variable.
     * It uses the same calculation as the calcDenominator on the outcomes that are not given.
     */
    private double calcComplement() {
        //initializing the final answer variable:
        double final_ANSWER = 0;

        //Adding all the different complement outcomes (that are not given) to the list:
        ArrayList<String> complementOutcomes = new ArrayList<>();
        for (String outcome : _dc._queryVariable.getOutcomes())
            if (!outcome.equals(_dc._queryVarValue))
                complementOutcomes.add(outcome);

        //Looping through the complement outcomes and changing the temp map accordingly:
        for (String outcome : complementOutcomes) {
            for (HashMap<String, String> tempMap : _valueListByMap) {
                //changing the value of the temp map to the complement value:
                tempMap.replace(_dc._queryName, outcome);
                double tempMapSUM = 1;
                //looping through each variable of the query to find its probability.
                next_var:
                //(breakpoint)
                for (Variable currVar : _variableList) {
                    //creating a map for all of its dependencies:
                    HashMap<String, String> varDependencyMap = new HashMap<>();
                    //inserting the current variables value:
                    varDependencyMap.put(currVar.getName(), tempMap.get(currVar.getName()));
                    //looping through the current variables parents and adding the dependencies:
                    for (Variable parent : currVar.getParents()) {
                        varDependencyMap.put(parent.getName(), tempMap.get(parent.getName()));
                    }
                    //looping through the current variables CPT to find the correct line:
                    cpt_line:
                    //(breakpoint)
                    for (HashMap<String, String> cpt_line_Map : currVar.getCPT()._cpt_table) {
                        for (Map.Entry<String, String> condVarKeyVal : varDependencyMap.entrySet()) {
                            //checking if the dependency keys hold different values. If so, go to next CPT line:
                            if (!condVarKeyVal.getValue().equals(cpt_line_Map.get(condVarKeyVal.getKey())))
                                continue cpt_line;
                        }
                        //If reached here, all condVarKeyVal key and values equal. FOUND CORRECT CPT LINE!
                        //Add the Probability to the total sum:
                        tempMapSUM *= Double.parseDouble(cpt_line_Map.get("Prob"));
                        continue next_var;
                    }
                }
                System.out.println(final_ANSWER + " + " + tempMapSUM + " = " + (final_ANSWER + tempMapSUM));
                //adding the tempMapSum to the final answer:
                final_ANSWER += tempMapSUM;

                //Switching the tempMap variable outcome back to the original query outcome to avoid future problems:
                tempMap.replace(_dc._queryName, _dc._queryVarValue);
            }
        }
        System.out.println("\t\t\t\t\tANS------" + final_ANSWER);
        return final_ANSWER;
    }
}
