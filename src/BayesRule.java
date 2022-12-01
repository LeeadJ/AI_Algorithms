import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BayesRule {

    public ArrayList<Variable> _variableList;
    public DataCleaner _dc;
    private String _answer = "No answer calculated";
    public int _mulNum=0;
    public int _addNum=0;
    public ArrayList<String[]> _permutations;

    public BayesRule(ArrayList<Variable> varList, DataCleaner _dc){
        _variableList = varList;
        this._dc = _dc;
        _mulNum = calcMul();
        _addNum = calcAdd();
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
    public String getAnswer(){
        this.calculateQuery();
        return _answer;
    }

    private void calculateQuery(){
        _answer = "";
        double denominator = calcDenominator();
        double complement = calcComplement();
        double probability = denominator / (denominator + complement);
        NumberFormat format_5_digits = new DecimalFormat("#0.00000");
        String formatted_prob = format_5_digits.format(probability);
        _answer = formatted_prob+","+_addNum+","+_mulNum;
    }

    private double calcDenominator() {

        return 0;
    }
    private double calcComplement() {

        return 0;
    }
    public ArrayList<ArrayList<String>> hiddenOutcomePermList(){
        ArrayList<ArrayList<String>> ans = getAllPermutations();
        return ans;
    }

    public ArrayList<ArrayList<String>> getAllPermutations() {
        ArrayList<ArrayList<String>> final_perm_array = new ArrayList<>();
        ArrayList<String> outcomeList = new ArrayList<>();
        //loop through the hidden variables:
        for(String name : _dc._hiddenList)
            //finding a match between hidden variable name and actual variable type:
            for(Variable var : _variableList)
                if(name.equals(var.getName()))
                    //loop through the outcomes of the hidden variable:
                    for(String out : var.getOutcomes())
                        //check if outcomeList contains outcome. If not, add:
                        if(!outcomeList.contains(out))
                            outcomeList.add(var.getName()+"="+out);


        permute(outcomeList, 0, final_perm_array);


        return final_perm_array;
    }

    private static <T> void permute(ArrayList<T> list, int index, ArrayList<ArrayList<T>> result) {
        for (int i = index; i < list.size(); i++) {
            Collections.swap(list, i, index);
            permute(list, index + 1, result);
            Collections.swap(list, index, i);
        }
        if (index == list.size() - 1) {
            result.add(new ArrayList<T>(list));
        }
    }


}
