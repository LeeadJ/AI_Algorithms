import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
public class getInput {
    private String _filePath;
    public ArrayList<String> _container;
    public String _xmlPath;

    /**Constructor for getInput
     * @param input - path of txt file.*/
    public getInput(String input){
        this._filePath = input;
        this._container = new ArrayList<>();
    }
}
