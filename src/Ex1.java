import java.util.ArrayList;

public class Ex1 {
    private static final char ONE = '1';
    private static final char TWO = '2';
    private static final char THREE = '3';

    public static void main(String[] args) {
        InputParser in = new InputParser("testInput.txt");
//        InputParser in = new InputParser("input.txt");
        in.extractFile();
        xmlParser xml_file = new xmlParser(in._xml_Path);
        ArrayList<Variable> variableList = xml_file.parse_file();
        for (String query : in._input_queries) {
            //Creating a DataCleaner object. This will Clean and organize the data accordingly:
            DataCleaner dc = new DataCleaner(query, variableList);
            System.out.println("-------------------------------------");
            System.out.println(query);
//            System.out.println("_number: "+dc._number);
//            System.out.println("_queryVar: "+dc._queryName);
//            System.out.println("_queryVarValue: "+dc._queryVarValue);
            System.out.println("_evidenceVarValList: "+dc._evidenceVarValList + "  size="+dc._evidenceVarValList.size());
//            System.out.println("_evidenceList: "+dc._evidenceList);
//            System.out.println("_evidenceValList: "+dc._evidenceValList);
//            System.out.println("_evAndQMap: "+dc._evAndQMap);
//            System.out.println("_hiddenList: "+dc._hiddenList+ "  size="+dc._hiddenList.size());
            System.out.print("Variables: [");
            for(Variable var : dc._variableList)
                System.out.print(var.getName() + " ");
            System.out.println("]"+ "  size="+dc._variableList.size());
//            System.out.println("_outcome_combination_num: "+dc._outcome_combination_num);
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
//                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                    System.out.println(br.calcComplement());
//                    System.out.println("\n\n br.getAnswer(): "+ br._answer+"\n\n");
                    CreateOutput.addLine(br._answer);
                    break;
                case TWO:
                    //VE
                    Variable_Elimination_ABC_Order ve = new Variable_Elimination_ABC_Order(dc);
//                    System.out.println("ANSWER: "+ve._answer);
//                    System.out.println("\n\nADDITION: "+ve._additionCounter);
//                    System.out.println("\n\nMULTIPLICATION: "+ve._multiplyCounter);
                    CreateOutput.addLine(ve._answer);
                    break;
                case THREE:
                    //VE heuristic
//                    Variable_Elimination_Heuristic_Order veH = new Variable_Elimination_Heuristic_Order(dc);
//                    CreateOutput.addLine(veH._answer);
                    break;
            }
            try{
                CreateOutput.writeToFile();
            } catch(NullPointerException e){
                throw new NullPointerException("Error Ex1: Line to write is empty");
            }

        }

    }
}
