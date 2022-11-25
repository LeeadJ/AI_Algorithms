import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class xml_Parser {
    public static void main(String[] args) {
        try{
            //creating object of type file, in order to read the xml file.
            File xmlDoc = new File("big_net.xml");
            //creating object of dbFact in order to work with DOM parser:
            DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuild = dbFact.newDocumentBuilder();
            //using the doc builder in order to parse the xml file in its standard.
            Document doc = dBuild.parse(xmlDoc);

            //Read root element
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
