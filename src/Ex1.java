import java.util.ArrayList;

public class Ex1 {
    private static final char ONE = '1';
    private static final char TWO = '2';
    private static final char THREE = '3';

    public static void main(String[] args) {
//        InputParser in = new InputParser("testInput.txt");
        InputParser in = new InputParser("input.txt");
        in.extractFile();
        xmlParser xml_file = new xmlParser(in._xml_Path);
        ArrayList<Variable> variableList = xml_file.parse_file();
        for (String query : in._input_queries) {
            //Creating a DataCleaner object. This will Clean and organize the data accordingly:
            DataCleaner dc = new DataCleaner(query, variableList);
//            System.out.println(query);
//            System.out.println("_number: "+dc._number);
//            System.out.println("_queryVar: "+dc._queryName);
//            System.out.println("_queryVarValue: "+dc._queryVarValue);
//            System.out.println("_evidenceVarValList: "+dc._evidenceVarValList);
//            System.out.println("_evidenceList: "+dc._evidenceList);
//            System.out.println("_evidenceValList: "+dc._evidenceValList);
//            System.out.println("_hiddenList: "+dc._hiddenList);
//            System.out.println("_outcome_combination_num: "+dc._outcome_combination_num);
//            System.out.println("-------------------------------------");
            switch (dc._number) {
                case ONE:
                    //BayesRule
                    BayesRule br = new BayesRule(variableList, dc);
//                    System.out.println(br._additionCounter);
//                    System.out.println(br._multiplyCounter);
//                    System.out.println("_fullVarList  : "+br._fullVarList);
//                    System.out.println("_fullValueList: "+br._fullValueList);
//                    System.out.println("_valueListbyMap: "+br._valueListByMap);
//                    System.out.println("*******Permutation List: "+br._permutations);
//                    System.out.println(br.calcDenominator());
                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    System.out.println(br.calcComplement());
//                    System.out.println("\n\n br.getAnswer(): "+ br._answer+"\n\n");
                    CreateOutput.addLine(br._answer);
                    break;
                case TWO:
                    //VE
//                    CreateOutput.addLine(...);
                    break;
                case THREE:
                    //VE heuristic
//                    CreateOutput.addLine(...);
                    break;
            }

            CreateOutput.writeToFile();
        }
        for (Variable var : variableList)
            System.out.println(var.toString());

//        InputParser in = new InputParser("input.txt");
//        in.extractFile();
//        xmlParser xml_file = new xmlParser(in._xml_Path);
//        xmlParser xml_file = new xmlParser("big_net.xml");
//        ArrayList<Variable> arr = xml_file.parse_file();
//        for(Variable var : arr)
//            System.out.println(var.toString());


//        System.out.println(arr.get(2).getCPT()._cpt_table.get(0));
//        System.out.println(arr.get(2).getCPT()._cpt_table.get(0).keySet());
//        System.out.println(arr.get(2).getCPT()._cpt_table.get(0).values());
//        System.out.println(in._input_queries.get(0));
//
//        for(String query : in._input_queries) {
//            char number = query.charAt(query.length() - 1);
//            query = query.substring(0, query.length() - 2);
//            System.out.println(number);
//            System.out.println(query);
//        }
    }
}
