/**
 *
 */
package roslab.processors.electronics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import roslab.model.electronics.Circuit;
import roslab.model.electronics.Pin;

/**
 * Parse EAGLE Schematics (XML)
 *
 * @author ?
 */
public class EagleSchematic {

    private File schematic = null;
    private List<String> nets = new ArrayList<String>();

    /**
     * Construct an EAGLE Schematic object from the input file.
     *
     * @param schematic
     */
    public EagleSchematic(File schematic) {
        this.schematic = schematic;

        // TODO Parse file and store nets in nets field.
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(this.schematic);

            // optional, but recommended
            // read this -
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("staff");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("Staff id : " + eElement.getAttribute("id"));
                    System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
                    System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the schematic's name
     */
    public String getName() {
        // Return the schematic filename without the '.sch' file extension
        return schematic.getName().substring(0, schematic.getName().lastIndexOf('.'));
    }

    /**
     * @return the schematic
     */
    public File getSchematicFile() {
        return schematic;
    }

    /**
     * @return the nets
     */
    public List<String> getNets() {
        return nets;
    }

    /**
     * @param net
     *            Name of the net to be renamed.
     * @param newName
     *            Name to which the net should be renamed.
     */
    private void setNet(String net, String newName) {
        // TODO Set the input net's name to the newName value.
    }

    /**
     * @param net
     *            Name of the net whose verification data is returned.
     */
    public Map<String, String> getVerificationData(String net) {
        // TODO Extract verification data from schematic, return in mapping of
        // key-value pairs (ie. <"voltage", "5">, <"current", "0.5"> )
        return null;
    }

    /**
     * @param output
     *            File where the schematic will be saved.
     */
    public void save(File output) {
        // TODO Save schematic to given File object.
    }

    /**
     * @param schematics
     *            List of schematics that will be merged into one.
     */
    public static EagleSchematic merge(List<EagleSchematic> schematics) {
        // TODO Merge all input schematics into a single schematic.
        return null;
    }

    /**
     * @param schematicNetMap
     *            Map of schematics to the net (in that schematic) which is to
     *            be renamed.
     * @param newName
     *            Name to which the nets in the map should be renamed.
     */
    public static void connect(Map<EagleSchematic, String> schematicNetMap, String newName) {
        // TODO Rename the net in each schematic to the new name.
        // Use schematic.setNet(net, newName)...
    }

    public static Circuit buildCircuitFromSchematic(EagleSchematic sch) {
        Circuit c = new Circuit(sch.getName());
        c.setSchematic(sch);
        for (String net : sch.getNets()) {
            c.addPin(Pin.getPinFromString(net, c));
        }
        return c;
    }

}
