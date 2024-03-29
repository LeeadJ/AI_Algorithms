import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class xmlParser {
    /**
     * This is the XML parser class. It extracts the information from the XML file and updates the data accordingly
     * 1) _FILENAME: The file path of the xml file.
     */
    private final String _FILENAME;

    /**
     * A Constructor for the cmlParser class.
     *
     * @param FILENAME - the xml file path.
     */
    public xmlParser(String FILENAME) {
        _FILENAME = FILENAME;
    }

    /**
     * This function parses the file and returns a variable list of all the given variables.
     */
    public ArrayList<Variable> parse_file() {
        //Initializing the variable lists:
        ArrayList<Variable> variable_list = new ArrayList<>();
        ArrayList<String> variableName_list = new ArrayList<>();

        try {
            //creating object of type file, in order to read the xml file.
            File xmlDoc = new File(_FILENAME);
            //creating object of dbFact in order to work with DOM parser:
            DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuild = dbFact.newDocumentBuilder();
            //using the doc builder in order to parse the xml file in its standard.
            Document doc = dBuild.parse(xmlDoc);

            //Parsing the first set of information in the xml file ("VARIABLES", "NAME", "OUTCOME")
            NodeList nList = doc.getElementsByTagName("VARIABLE");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                //reading the attributes of each Node (variable):
                //Only if the child of the node is an element:
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nNode;
                    Variable var = new Variable(elem.getElementsByTagName("NAME").item(0).getTextContent());
                    int iter = 0;
                    while (elem.getElementsByTagName("OUTCOME").item(iter) != null) {
                        var.addOutcomes(elem.getElementsByTagName("OUTCOME").item(iter).getTextContent());
                        iter++;
                    }
                    variable_list.add(var);
                    variableName_list.add(var.getName());
                }
            }
            //Parsing the second set of information in the xml file ("DEFINITION", "FOR", "GIVEN", "TABLE")
            NodeList nodeList2 = doc.getElementsByTagName("DEFINITION");
            for (int i = 0; i < nodeList2.getLength(); i++) {
                Node node = nodeList2.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    Variable var = variable_list.get(variableName_list.indexOf(elem.getElementsByTagName("FOR").item(0).getTextContent()));
                    int iter = 0;
                    while (elem.getElementsByTagName("GIVEN").item(iter) != null) {
                        String str = elem.getElementsByTagName("GIVEN").item(iter).getTextContent();
                        Variable parent = variable_list.get(variableName_list.indexOf(str));
                        var.addParent(parent);
                        parent.addChild(var);
                        iter++;
                    }
                    //Adding the probability table information:
                    iter = 0;
                    String prob_table = "";
                    while (elem.getElementsByTagName("TABLE").item(iter) != null) {
                        prob_table += elem.getElementsByTagName("TABLE").item(iter++).getTextContent();
                        iter++;
                    }
                    var.initCPT(prob_table.split(" "));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return variable_list;
    }
}
