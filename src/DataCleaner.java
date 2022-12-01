import java.util.ArrayList;
import java.util.stream.IntStream;

/** This class will receive a query row, and clean the data according to certain variables.
 *  The DataCleaner Class will hold six variables:
 * 1) _queryVar: The query variable.
 * 2) _queryVarValue: The value of the query variable.
 * 3) _evidenceList: An Arraylist of type string representing the evidence variables.
 * 4) _evidenceValList: An Arraylist of type string representing the evidence variable values.
 * 5) _number: The number of the query representing the wanted algorithm
 * 6) _hiddenList - A list of the hidden variables.*/
public class DataCleaner {

    public String _queryVar;
    public String _queryVarValue;
    public ArrayList<String> _evidenceList;
    public ArrayList<String> _evidenceValList;
    public char _number;
    public ArrayList<String> _hiddenList;
    public int _outcome_combination_num;

    /** This Constructor is in charge of cleaning the data.
     * @param query - A single row from the input file. */
    public DataCleaner(String query, ArrayList<Variable> _variableList){
        //Extracting the number from the query:
        _number = query.charAt(query.length() - 1);

        //Deleting unwanted letters from the query.
        query = query.substring(0,query.length()-2);
        query = query.replace("P(","");
        query = query.replace(")","");

        //Extracting the rest of the parameters:
        _evidenceList = new ArrayList<>();
        _evidenceValList = new ArrayList<>();
        String[] query_and_evidence_split = query.split("[|]");
        _queryVar = query_and_evidence_split[0];
        String[] queryVar_and_value_split = _queryVar.split("=");
        _queryVarValue = queryVar_and_value_split[1];
        if(query_and_evidence_split.length>1 && query_and_evidence_split[1].length() > 0){
            String[] evidence = query_and_evidence_split[1].split(",");
            for(String index : evidence){
                String[] index_values = index.split("=");
                _evidenceList.add(index_values[0]);
                _evidenceValList.add(index_values[1]);
            }
        }
        _hiddenList = calcHidden(_variableList);

        int num = 1;
        for(String name : _hiddenList)
            for(Variable var : _variableList)
                if(name.equals(var.getName()))
                    num *= var.getOutcomes().size();
        _outcome_combination_num = num;
    }

    public ArrayList<String> calcHidden(ArrayList<Variable> variableList){
        ArrayList<String> hidden = new ArrayList<>();
        String[] arr = _queryVar.split("=");
        for(Variable var : variableList){
            if(!_evidenceList.contains(var.getName()) && !var.getName().equals(arr[0]))
                hidden.add(var.getName());
        }
        return hidden;
    }
//    public static void main(String[] args) {
//        String q = "P(B=v1|J=T,M=T),1";
//        DataCleaner dc = new DataCleaner(q);
//        System.out.println(dc._number);
//        System.out.println(dc._queryVar);
//        System.out.println(dc._queryVarValue);
//        System.out.println(dc._evidenceList);
//        System.out.println(dc._evidenceValList);
////        System.out.println(Arrays.toString(dc._evidenceList));
//    }
}
