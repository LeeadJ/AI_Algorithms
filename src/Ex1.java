import java.util.ArrayList;
public class Ex1 {
    private static final char ONE = '1';
    private static final char TWO = '2';
    private static final char THREE = '3';
    public static void main(String[] args) {


        InputParser in = new InputParser("input.txt");
        in.extractFile();
        xmlParser xml_file = new xmlParser(in._xmlPath);
//        xml_Parser xml_file = new xml_Parser("big_net.xml");
        ArrayList<Variable> arr = xml_file.parse_file();
        for(Variable var : arr)
            System.out.println(var.toString());

        System.out.println("Container check-----------");
        int j=1;
        for(String v : in._input_queries){
            System.out.println("Querie "+(j++)+": "+v);
        }

        System.out.println("\nFile Path: ");

    }
}
