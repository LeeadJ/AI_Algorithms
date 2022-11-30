import java.util.ArrayList;

/** This class will receive a query row, and clean the data according to certain variables.*/
public class DataCleaner {

    public String _queryVar;
    public String _queryVarValue;
    public ArrayList<String> _evidenceList;
    public ArrayList<String> _evidenceValList;

    public DataCleaner(String query){
        query = query.replace("P(","");
        query = query.replace(")","");
        _evidenceList = new ArrayList<>();
        _evidenceValList = new ArrayList<>();
        String[] query_and_evidence_split = query.split("[|]");
        _queryVar = query_and_evidence_split[0];
        String[] queryvar_and_value_split = _queryVar.split("=");
        _queryVarValue = queryvar_and_value_split[1];
        if(query_and_evidence_split.length>1 && query_and_evidence_split[1].length() > 0){
            String[] evidence = query_and_evidence_split[1].split(",");
            for(String index : evidence){
                String[] index_values = index.split("=");
                _evidenceList.add(index_values[0]);
                _evidenceValList.add(index_values[1]);
            }
        }
    }

    public static void main(String[] args) {
        String q = "P(B=v1|J=T,M=T)";
        DataCleaner dc = new DataCleaner(q);
        System.out.println(dc._queryVar);
        System.out.println(dc._queryVarValue);
        System.out.println(dc._evidenceList);
        System.out.println(dc._evidenceValList);
//        System.out.println(Arrays.toString(dc._evidenceList));
    }
}
