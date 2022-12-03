import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class BayesRule {

    public ArrayList<Variable> _variableList;
    public DataCleaner _dc;
    public String _answer = "No answer calculated";
    public int _mulNum=0;
    public int _addNum=0;
    public ArrayList<ArrayList<String>> _permutations;
    public ArrayList<ArrayList<String>> _fullVarList;
    public ArrayList<ArrayList<String>> _fullValueList;
    public ArrayList<HashMap<String, String>> _valueListbyMap;
    public double _denominator;
//    public int mulTest=0;
//    public int addTest=0;


    /** Constructor for the BayesRule class. */
    public BayesRule(ArrayList<Variable> varList, DataCleaner _dc){
        _variableList = varList;
        this._dc = _dc;
        _mulNum = calcMul();
        _addNum = calcAdd();
        _permutations = getAllPermutations();
        _fullValueList = new ArrayList<>();
        _fullVarList = new ArrayList<>();
        for(ArrayList<String> temp : _permutations){
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
        _valueListbyMap = new ArrayList<>();
        for(int i=0; i<_fullVarList.size(); i++){
            HashMap<String, String> temp = new HashMap<>();
            for(int j=0; j<_fullVarList.get(i).size(); j++){
                String var = _fullVarList.get(i).get(j);
                temp.put(var, _fullValueList.get(i).get(j));
            }
            _valueListbyMap.add(temp);
        }

        _answer = calculateQuery();
    }

    /** This function calculates the amount of multiplication steps needed to solve the query.
     * Logic: (num of variables -1) * (num of outcome combinations) * (size of outcome list) [-because of the normalization].*/
    private int calcMul() {
        return (_variableList.size() -1)*(_dc._outcome_combination_num)*(_dc._queryVariable.getOutcomes().size());
    }

    /** This function calculates the amount of addition steps needed to solve the query.
    * Logic: (num of outcome combinations) * (size of outcome list) [-because of the normalization] + (1) [-because of the numerator]*/
    private int calcAdd() {
        return (_dc._outcome_combination_num - 1) * (_dc._queryVariable.getOutcomes().size()) + (1);
    }

    /** This function returns the answer of the query. */


    /** This function calculates the query and saves the line in the _answer variable. */
    private String calculateQuery(){
        _answer = "";
        _denominator = calcDenominator();
        double com = calcComplement();
        double probability = _denominator / (com + _denominator);
//        addTest++;
        NumberFormat format_5_digits = new DecimalFormat("#0.00000");
        String formatted_prob = format_5_digits.format(probability);
        return formatted_prob+","+_addNum+","+_mulNum;
//        System.out.println("Step 1 DEN: "+_denominator);
//        System.out.println("Step 2: NUM: "+com);
//        _answer = ""+probability;
    }


    public double calcDenominator() {
        System.out.println("\t\tCalcDenominator");
        double final_ANSWER = 0;
        int addCounter=-1;
        int mulCounter=0;

        for(HashMap<String, String> tempMap : _valueListbyMap){
            int tempMulcounter=-1;
            double tempMapSUM = 1;
//            System.out.println("tempMap: "+tempMap);
            //looping through each variable of the query to find its probability.
            next_var: for(Variable currVar : _variableList){
                //creating a map for all of its dependencies:
                HashMap<String, String> varDependencyMap = new HashMap<>();
                //inserting the current variables value:
                varDependencyMap.put(currVar.getName(), tempMap.get(currVar.getName()));
                //looping through the current variables parents and adding the dependencies:
                for(Variable parent : currVar.getParents()){
                    varDependencyMap.put(parent.getName(), tempMap.get(parent.getName()));
                }
//                System.out.println(currVar.getName()+"  varConditionMap---"+ varDependencyMap);
                //looping through the current variables CPT to find the correct line:
                cpt_line: for(HashMap<String, String> cpt_line_Map : currVar.getCPT()._cpt_table){
                    for(Map.Entry<String, String> condVarKeyVal : varDependencyMap.entrySet()){
                        //checking if the dependency keys hold different values. If so, go to next CPT line:
                        if(!condVarKeyVal.getValue().equals(cpt_line_Map.get(condVarKeyVal.getKey())))
                            continue cpt_line;
                    }
                    //If reached here, all condVarKeyVal key and values equal. FOUND CORRECT CPT LINE!
                    //Add the Probability to the total sum:
//                    System.out.println("CPT-LINE"+cpt_line_Map);
                    tempMapSUM *= Double.parseDouble(cpt_line_Map.get("Prob"));
                    tempMulcounter++;
//                    System.out.println(cpt_line_Map.get("Prob"));
                    continue next_var;
                }
            }
//            System.out.println(final_ANSWER +" + "+tempMapSUM+" = "+(final_ANSWER +tempMapSUM));
            final_ANSWER += tempMapSUM;
            addCounter++;
//            addTest += addCounter;
            mulCounter += tempMulcounter;
//            mulTest += mulCounter;
        }
        System.out.println("\t\t\t\t\tANS------"+ final_ANSWER);
        System.out.println("\t\t\t\t\tmulCounter------"+mulCounter);
        System.out.println("\t\t\t\t\taddCounter------"+addCounter);
        _denominator = final_ANSWER;
        return final_ANSWER;
    }
    public double calcComplement() {
        int loop = 1;
        System.out.println("\t\tcalcComplement");
        double final_ANSWER = 0;
        int addCounter=-1;
        int mulCounter=0;

        //Adding all the different complement outcomes to the list:
        ArrayList<String> complementOutcomes = new ArrayList<>();
        for(String outcome : _dc._queryVariable.getOutcomes())
            if(!outcome.equals(_dc._queryVarValue))
                complementOutcomes.add(outcome);
        System.out.println("Loop: "+loop++);
        //Looping through the complement outcomes and changing the temp map accordingly:
        for(String outcome : complementOutcomes) {
            System.out.println("OUTCOME: "+outcome);
            for (HashMap<String, String> tempMap : _valueListbyMap) {
                //changing the value of the temp map to the complement value:
                tempMap.replace(_dc._queryName, outcome);
                int tempMulcounter = -1;
                double tempMapSUM = 1;
                System.out.println("tempMap: " + tempMap);
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
                    System.out.println(currVar.getName() + "  varConditionMap---" + varDependencyMap);
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
                        System.out.println("CPT-LINE" + cpt_line_Map);
                        tempMapSUM *= Double.parseDouble(cpt_line_Map.get("Prob"));
                        tempMulcounter++;
                        System.out.println(cpt_line_Map.get("Prob"));
                        continue next_var;
                    }
                }
                System.out.println(final_ANSWER + " + " + tempMapSUM + " = " + (final_ANSWER + tempMapSUM));
                final_ANSWER += tempMapSUM;
//                addCounter++;
//                addTest += addCounter;
//                mulCounter += tempMulcounter;
//                mulTest += mulCounter;

                //Switching the tempMap variable outcome back to the original query outcome:
                tempMap.replace(_dc._queryName, _dc._queryVarValue);
            }
        }
        System.out.println("\t\t\t\t\tANS------"+ final_ANSWER);
//        System.out.println("\t\t\t\t\tmulCounter------"+mulCounter);
//        System.out.println("\t\t\t\t\taddCounter------"+addCounter);
//        _denominator = final_ANSWER;

        return final_ANSWER;
    }

    /** This function generates and returns a cleaner permutation list according to the wanted outcomes.*/
    public ArrayList<ArrayList<String>> getAllPermutations() {
        ArrayList<String> outcomeList = new ArrayList<>();
        //loop through the hidden variables:
        for(String name : _dc._hiddenList)
            //finding a match between hidden variable name and actual variable type:
            for(Variable var : _variableList)
                if(name.equals(var.getName())) {
                    //loop through the outcomes of the hidden variable:
                    for (String out : var.getOutcomes())
                        //check if outcomeList contains outcome. If not, add:
                        if (!outcomeList.contains(out))
                            outcomeList.add(out);
                break;
                }
        ArrayList<ArrayList<String>> final_list = generate_permutations(outcomeList, outcomeList.size(), _dc._hiddenList.size());
        //Removing the unwanted permutations:
//        System.out.println(final_list.size());
//        System.out.println(final_list);
        int index=0;
        outer: while(index<final_list.size()){
            ArrayList<String> temp = final_list.get(index);
            //Looping through the hidden variables:
            for(int i=0; i<temp.size(); i++){
                String temp_value = temp.get(i);
                String hidden_name = _dc._hiddenList.get(i);
                //finding the variable:
                for(Variable var : _variableList){
                    //matching hidden name with its variable type.
                    if(var.getName().equals(hidden_name)){
                        //checking if the hidden variable contains the outcome at the index of the temp list:
                        if(!var.getOutcomes().contains(temp_value)){
                            final_list.remove(temp);
                            continue outer;
                        }
//                        temp.set(i, hidden_name+"="+temp_value);
                        break;
                    }
                }
            }
            index++;
        }
//        for(ArrayList<String> a : final_list)
//            System.out.println(a);
//        System.out.println(final_list.size());
        return final_list;
    }

    /** This function generates all the permutations of a given array.
     * @param outcomes - An ArrayList containing the type of outcomes to permute.
     * @param list_size - The size of the outcomes list.
     * @param perm_size - the size of the wanted permutations.*/
    public ArrayList<ArrayList<String>> generate_permutations(ArrayList<String> outcomes, int list_size, int perm_size){
        ArrayList<ArrayList<String>> flist = new ArrayList<>();
        for(int i=0; i < (int)Math.pow(list_size, perm_size); i++){
            int index = i;
            ArrayList<String> alist = new ArrayList<>();
            for (int j = 0; j < perm_size; j++){
                alist.add(outcomes.get(index % list_size));
                index /= list_size;
            }
            flist.add(alist);
        }
        return flist;
    }
}
