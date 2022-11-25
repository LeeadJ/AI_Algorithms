import java.io.*;
import java.util.ArrayList;
public class Ex1 {
    public static void main(String[] args) {

//        xml_Parser xml = new xml_Parser("alarm_net.xml");
        xml_Parser xml = new xml_Parser("big_net.xml");
        ArrayList<Variable> arr = xml.parse_file();
        for(Variable var : arr)
            System.out.println(var.toString());
    }
}
