import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class BayesRule {

    public ArrayList<Variable> _variableList;
    public DataCleaner _dc;
    public String _answer;
    public int _multiplyCounter;
    public int _additionCounter;
    public ArrayList<ArrayList<String>> _permutations;
    public ArrayList<ArrayList<String>> _fullVarList;
    public ArrayList<ArrayList<String>> _fullValueList;
    public ArrayList<HashMap<String, String>> _valueListByMap;
    public double _denominator;
    public double _numerator;
//    public int mulTest=0;
//    public int addTest=0;


    /**
     * Constructor for the BayesRule class.
     */
    public BayesRule(ArrayList<Variable> varList, DataCleaner _dc) {
        _variableList = varList;
        this._dc = _dc;
        _multiplyCounter = calcMul();
        _additionCounter = calcAdd();
        _permutations = _dc._permutationList;
        _fullValueList = new ArrayList<>();
        _fullVarList = new ArrayList<>();
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

        _answer = calculateQuery();
    }

    /**
     * This function calculates the amount of multiplication steps needed to solve the query.
     * Logic: (num of variables -1) * (num of outcome combinations) * (size of outcome list) [-because of the normalization].
     */
    private int calcMul() {
        return (_variableList.size() - 1) * (_dc._outcome_combination_num) * (_dc._queryVariable.getOutcomes().size());
    }

    /**
     * This function calculates the amount of addition steps needed to solve the query.
     * Logic: (num of outcome combinations) * (size of outcome list) [-because of the normalization] + (1) [-because of the numerator]
     */
    private int calcAdd() {
        return (_dc._outcome_combination_num - 1) * (_dc._queryVariable.getOutcomes().size()) + (1);
    }

    /** This function returns the answer of the query. */


    /**
     * This function calculates the query and return the answer.
     */
    private String calculateQuery() {
        //calculating the denominator and numerator:
        _denominator = calcDenominator();
        _numerator = calcComplement() + _denominator;
        //calculating the probabilitiy:
        double probability = _denominator / _numerator;
        //formatting the probability to 5th decimal:
        NumberFormat format_5_digits = new DecimalFormat("#0.00000");
        String formatted_prob = format_5_digits.format(probability);
        return formatted_prob + "," + _additionCounter + "," + _multiplyCounter;
    }


    public double calcDenominator() {
//        System.out.println("\t\tCalcDenominator");
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
     * This function calculates the numerator. It loops throught the different outcomes of the query variable,
     * and uses the same calculation as the calcDenominator on the outcomes that are not given.
     */
    public double calcComplement() {
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
