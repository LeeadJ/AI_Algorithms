import java.util.ArrayList;

//TODO: add try/catch to the query reader and to each algorithm (so the answer will return a line of error)

public class Ex1 {
    public static void main(String[] args) {
//        InputParser in = new InputParser("testInput.txt");

        InputParser in = new InputParser("input.txt");
        in.extractFile();
        xmlParser xml_file = new xmlParser(in._xml_Path);
        ArrayList<Variable> variableList = xml_file.parse_file();
        for (String query : in._input_queries) {
            //Creating a DataCleaner object. This will Clean and organize the data accordingly:
            DataCleaner dc = new DataCleaner(query, variableList);
//            System.out.println("-------------------------------------");
//            System.out.println(query);
//            System.out.println("_evidenceVarValList: " + dc._evidenceVarValList + "  size=" + dc._evidenceVarValList.size());
//            System.out.print("Variables: [");
//            for (Variable var : dc._variableList)
//                System.out.print(var.getName() + " ");
//            System.out.println("]" + "  size=" + dc._variableList.size());
            if (dc._number == '1') {
                //BayesRule
                try {
                    BayesRule br = new BayesRule(variableList, dc);
                    CreateOutput.addLine(br._answer);
                } catch (Exception e) {
                    CreateOutput.addLine("Error calculating: " + query);
                }
            } else {
                //VE
                try {
                    Variable_Elimination ve = new Variable_Elimination(dc);
                    CreateOutput.addLine(ve._answer);
                } catch (Exception e) {
                    CreateOutput.addLine("Error calculating: " + query);
                }
            }
            try {
                CreateOutput.writeToFile();
            } catch (Exception e) {
                throw new NullPointerException("Error Ex1: Can Not Create Output");
            }
        }

    }
}
