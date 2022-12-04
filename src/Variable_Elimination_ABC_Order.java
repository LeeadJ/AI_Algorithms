import java.util.ArrayList;
import java.util.HashMap;

public class Variable_Elimination_ABC_Order {

    public DataCleaner _dc;
    public ArrayList<Variable> _variableList;
    public String _answer;
    public int _multiplyCounter=0;
    public int _additionCounter=0;
    public ArrayList<Factor> _factorList;

    public Variable_Elimination_ABC_Order(DataCleaner dc){
        _dc = dc;
        _variableList = _dc._variableList;
        System.out.println("\n\t\tFactors");
        _factorList = cleanFactorList();
        for(Factor f : _factorList)
            System.out.println(f);
        System.out.println("Factor list size: "+_factorList.size());
        _answer = calculateQuery();
    }

    private String calculateQuery() {
        String ans = "sdfsdfsdf";
        return ans;
    }
    private ArrayList<Factor> cleanFactorList(){
        ArrayList<Factor> factorList = new ArrayList<>();
        next_var:
        for(Variable var : _variableList){
            if(_dc._evAndQMap.containsKey(var)){
                Factor currF = new Factor(var, _dc);
                if(currF._row_size>1)
                    factorList.add(currF);
            }
            else {
                for(Variable evAndQVar : _dc._evAndQMap.keySet()){
                    if(evAndQVar.isAncestor(var)) {
                        Factor currF = new Factor(var, _dc);
                        if (currF._row_size > 1)
                            factorList.add(new Factor(var, _dc));
                        continue next_var;
                    }
                }
            }

//            for(Variable evAndQVar : _dc._evAndQMap.keySet()){
//                //If the variable is an evidence or query variable create a Factor;
//                if(var.getName().equals(evAndQVar.getName())){
//                    factorList.add(new Factor(var, _dc));
//                    continue next_var;
//                }
//
//            }
        }
        return factorList;
    }
}
