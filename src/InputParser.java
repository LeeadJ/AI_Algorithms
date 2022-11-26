import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
public class InputParser {
    private final String _filePath;
    public ArrayList<String> _input_queries;
    public String _xmlPath;

    /**Constructor for getInput
     * @param input - path of txt file.*/
    public InputParser(String input){
        this._filePath = input;
        this._input_queries = new ArrayList<>();
    }

    /** This function extracts the queries from the input file and saves the xml path for the xml reader.*/
    public void extractFile() {
        try{
            FileReader fr = new FileReader(this._filePath);
            BufferedReader br = new BufferedReader(fr);
            this._xmlPath = br.readLine();
            String str = br.readLine();
            while(str != null) { ////////why str is not printing?
                this._input_queries.add(str);
                System.out.println(str);
                str = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
