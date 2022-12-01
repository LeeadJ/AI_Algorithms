import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class BayesRule {

    public ArrayList<Variable> _variableList;
    public DataCleaner _dc;
    private String _answer = "No answer calculated";
    public int _mulNum=0;
    public int _addNum=0;

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


}
