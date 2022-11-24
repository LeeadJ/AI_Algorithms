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
    private String _fileName;

    /** Constructor for xml_input: */
    public xmlParser(String file_name){
        this._fileName = file_name;
    }

    /** This function will parse the xml file and extract the variables/information it has.*/
    public ArrayList<Variable> createNet(){
        //Init an ArayList that will hold all the variables in the xml file.
        ArrayList<Variable> varList = new ArrayList<>();
        ArrayList<String> vars_names = new ArrayList<>();
        try{
            File file = new File(_fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            //parsing the xnl file by "VARIABLE". "NAME", "OUTCOME":
            NodeList nodes1 = doc.getElementsByTagName("VARIABLE");
            for(int i = 0; i< nodes1.getLength(); i++){
                Node node = nodes1.item(i);
                Variable var1 = null;
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element elem = (Element) node;
                    //Creating the variable by name:
                    var1 = new Variable(elem.getElementsByTagName("NAME").item(0).getTextContent());
                    int iter = 0;
                    while(elem.getElementsByTagName("OUTCOME").item(iter) != null){
                        //adding the outcomes of the variable to its outcome list:
                        var1.addOutcomes(elem.getElementsByTagName("OUTCOME").item(iter).getTextContent());
                        iter++;
                    }
                }
                varList.add(var1);
                vars_names.add(var1.getName());
            }
            //parsing the xml file by "DEFINITION", "FOR", "GIVEN", "TABLE":
            NodeList nodes2 = doc.getElementsByTagName("DEFINITION");
            for(int i=0; i<nodes2.getLength(); i++){
                Variable var2;
                String var_name = "";
                Variable var_par;
                Node node = nodes2.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element elem = (Element) node;
                    var2 = varList.get(vars_names.indexOf(elem.getElementsByTagName("FOR").item(0).getTextContent()));
                    int itr = 0;
                    while(elem.getElementsByTagName("GIVEN").item(itr)!=null){
                        var_name = elem.getElementsByTagName("GIVEN").item(itr).getTextContent();
                        var_par = varList.get(vars_names.indexOf(var_name));
                        var2.addParent(var_par);
                        var_par.addChild(var2);
                        itr++;
                    }
                    itr = 0;
                    String table = "";
                    while(elem.getElementsByTagName("TABLE").item(itr) != null){
                        table += elem.getElementsByTagName("TABLE").item(itr++).getTextContent();
                        itr++;
                    }
                    var2.initCPT(table.split(" "));
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return varList;
    }
}
