import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class BayesRule {

    public ArrayList<Variable> _variableList;
    public DataCleaner _dc;
    private String _answer = "No answer calculated";
    public int _mulNum=0;
    public int _addNum=0;
    public ArrayList<ArrayList<String>> _permutations;
    public ArrayList<ArrayList<String>> _fullVarList;
    public ArrayList<ArrayList<String>> _fullValueList;
    public ArrayList<HashMap<String, String>> _valueListbyMap;
    public double _denominator;

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
            curr1.add(_dc._queryVar);
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
                temp.put(var, var+"="+_fullValueList.get(0).get(j));
            }
            _valueListbyMap.add(temp);
        }
    }

    /** This function calculates the amount of multiplication steps needed to solve the query.
     * Logic: (num of variables -1) * (num of outcome combinations) * (2) [-because of the normalization].*/
    private int calcMul() {
        return (_variableList.size() -1)*(_dc._outcome_combination_num)*2;
    }

    /** This function calculates the amount of addition steps needed to solve the query.
    * Logic: (num of outcome combinations) * (2 - for the normalizer) + (1 - for the normalizer)*/
    private int calcAdd() {
        return (_dc._outcome_combination_num - 1) * (2) + (1);
    }

    /** This function returns the answer of the query. */
    public String getAnswer(){
        this.calculateQuery();
        return _answer;
    }

    /** This function calculates the query and saves the line in the _answer variable. */
    private void calculateQuery(){
        _answer = "";
        _denominator = calcDenominator();
        double complement = calcComplement();
        double probability = _denominator / (_denominator + complement);
        NumberFormat format_5_digits = new DecimalFormat("#0.00000");
        String formatted_prob = format_5_digits.format(probability);
        _answer = formatted_prob+","+_addNum+","+_mulNum;
    }


    private double calcDenominator() {
        double ans = 1;
        //Looping through the different combinations of the variables according to th query:
        for(int i=0; i<_valueListbyMap.size(); i++){
            HashMap<String, String> tempMap = _valueListbyMap.get(i);
            for(Variable var : _variableList){
                if(var.getParents().size() == 0){
                    String val = tempMap.get(var.getName());
                    for(HashMap<String, String> cpt_line_map : var.getCPT()._cpt_table){
                        /**Continue here: find value for vriable with no parents in the cpt*/
                        if(cp)
                    }

                }

            }
        }


        return 0;
    }
    private double calcComplement() {

        return 0;
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
