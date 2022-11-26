import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
public class InputParser {
    private final String _filePath; //(using final so it won't be changed)
    public ArrayList<String> _input_queries;
    public String _xmlPath;

    /**Constructor for InputParser
     * @param filePath - path of txt file.*/
    public InputParser(String filePath){
        _filePath = filePath;
        _input_queries = new ArrayList<>();
    }

    /** This function extracts the queries from the input file into an ArrayList<String>.
     *  It also saves the xml path for the use of the xmlParser.*/
    public void extractFile() {
        try{
            FileReader fr = new FileReader(_filePath);
            BufferedReader br = new BufferedReader(fr);
            _xmlPath = br.readLine();
            String str = br.readLine();
            while(str != null) {
                _input_queries.add(str);
                System.out.println(str);
                str = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
