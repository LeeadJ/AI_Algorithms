import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CreateOutput {
    private static ArrayList<String> _lines;

    public static void addLine(String line){
        if(_lines == null)
            _lines = new ArrayList<>();
        _lines.add(line + "\n");
    }

    public static void writeToFile(){
        try{
            FileWriter myWriter = new FileWriter("outputTest.txt");
            for(String toWrite : _lines){
                myWriter.write(toWrite);
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing file to: output.txt");
            e.printStackTrace();
        }
    }
}
