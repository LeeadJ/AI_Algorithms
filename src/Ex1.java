import java.util.ArrayList;

public class Ex1 {
    private static final char ONE = '1';
    private static final char TWO = '2';
    private static final char THREE = '3';
    public static void main(String[] args) {


        InputParser in = new InputParser("input.txt");
        in.extractFile();////
//        xmlParser xml_file = new xmlParser(in._xml_Path);
        xmlParser xml_file = new xmlParser("big_net.xml");
        ArrayList<Variable> arr = xml_file.parse_file();
        for(Variable var : arr){
            System.out.println(var.toString());
            for(String s : var.getOutcomes())
                CreateOutput.addLine(s);
        }

        CreateOutput.writeToFile();


    }
}
