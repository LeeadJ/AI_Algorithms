import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class InputParser {
    /**
     * The InputParser Class will hold three variables:
     * 1) _filePath: The path of the input file (i.e. "input.txt").
     * 2) _input_queries: An ArrayList of type String. Each index will contain a row (query) from the input file.
     * 3) _xml_Path: The path of the xml file.
     */
    private final String _filePath; //(using final so it won't be changed)
    public ArrayList<String> _input_queries;
    public String _xml_Path;

    /**
     * Constructor for InputParser
     *
     * @param filePath - path of txt file.
     */
    public InputParser(String filePath) {
        _filePath = filePath;
        _input_queries = new ArrayList<>();
    }

    /**
     * This function extracts the queries from the input file into an ArrayList<String>.
     * It also saves the xml path for the use of the xmlParser.
     */
    public void extractFile() {
        try {
            FileReader fr = new FileReader(_filePath);
            BufferedReader br = new BufferedReader(fr);
            _xml_Path = br.readLine();
            String query = br.readLine();
            while (query != null) {
                _input_queries.add(query);
//                System.out.println(query);
                query = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
