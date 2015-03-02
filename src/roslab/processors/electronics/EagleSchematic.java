/**
 *
 */
package roslab.processors.electronics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
 * @author Peter Gebhard
 */
public class EagleSchematic {

    private File schematic = null;

    // Map original net names to connected net names (map value is null if that
    // net is unconnected)
    private Map<String, String> nets = new HashMap<String, String>();

    /**
     * Construct an EAGLE Schematic object from the input file.
     *
     * @param schematic
     */
    public EagleSchematic(File schematic) {
        this.schematic = schematic;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(this.schematic);

            // optional, but recommended
            // read this -
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Schematic : " + this.schematic.getName());

            NodeList nList = doc.getElementsByTagName("net");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    nets.put(eElement.getAttribute("name"), null);
                    System.out.println("  Net : " + eElement.getAttribute("name"));
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
    public Map<String, String> getNets() {
        return nets;
    }

    /**
     * @param net
     *            Name of the net to be renamed.
     * @param newName
     *            Name to which the net should be renamed.
     */
    private void setNet(String net, String newName) {
        nets.put(net, newName);
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
    public static EagleSchematic merge(List<EagleSchematic> schematics, String filename) {
        if (schematics.size() < 2) {
            throw new IllegalArgumentException("Cannot merge schematics if the input contains less than 2.");
        }

        File mergedSch = new File(schematics.get(0).getSchematicFile().getParent() + File.separatorChar + filename);

        // Find which schematic file is the largest in line count; assuming that
        // one is the most important, choose it as the one to accept merges from
        // the other schematics.
        long largest = schematics.get(0).schematic.length();
        File largestSch = schematics.get(0).schematic;
        for (EagleSchematic sch : schematics) {
            if (sch.schematic.length() > largest) {
                largest = sch.schematic.length();
                largestSch = sch.schematic;
            }
        }

        // Copy contents of largest schematic to our merged output schematic
        // file.
        try {
            Files.copy(largestSch.toPath(), mergedSch.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Parse contents of the schematic where we want to merge the others
        // (and parse the others that are to be merged)
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document mDoc = dBuilder.parse(mergedSch);
            mDoc.getDocumentElement().normalize();
            Node mDocLayersNode = null;
            Node mDocLibrariesNode = null;
            Node mDocPackagesNode = null;
            Node mDocSymbolsNode = null;
            Node mDocDeviceSetsNode = null;
            Node mDocPartsNode = null;
            Node mDocSheetsNode = null;

            NodeList mDocLayerList = mDoc.getElementsByTagName("layer");
            if (mDocLayerList.getLength() > 0) {
                mDocLayersNode = mDocLayerList.item(0).getParentNode();
            }
            else {
                mDocLayersNode = mDoc.createElement("layers");
            }

            NodeList mDocLibraryList = mDoc.getElementsByTagName("library");
            if (mDocLibraryList.getLength() > 0) {
                mDocLibrariesNode = mDocLibraryList.item(0).getParentNode();
            }
            else {
                mDocLibrariesNode = mDoc.createElement("libraries");
            }

            NodeList mDocPartList = mDoc.getElementsByTagName("part");
            if (mDocPartList.getLength() > 0) {
                mDocPartsNode = mDocPartList.item(0).getParentNode();
            }
            else {
                mDocPartsNode = mDoc.createElement("parts");
            }

            // Do merging for each schematic
            for (EagleSchematic sch : schematics) {
                // Parse the schematic into a Document
                Document doc = dBuilder.parse(sch.schematic);
                doc.getDocumentElement().normalize();

                // An 'exists' flag used when checking if an Element already
                // exists in the merge document
                boolean exists = false;

                // Handle layers
                NodeList docLayerList = doc.getElementsByTagName("layer");
                for (int i = 0; i < docLayerList.getLength(); i++) {
                    // Check if layer exists already in merge document
                    for (int j = 0; j < mDocLayerList.getLength(); j++) {
                        if (((Element) mDocLayerList.item(j)).getAttribute("id").equals(((Element) docLayerList.item(i)).getAttribute("id"))) {
                            exists = true;
                        }
                    }
                    // If the layer does not exist in the merge document, add it
                    if (!exists) {
                        mDocLayersNode.appendChild(mDoc.importNode(docLayerList.item(i), true));
                    }
                    // Reset 'exists' flag
                    exists = false;
                }

                // Handle libraries
                NodeList docLibraryList = doc.getElementsByTagName("library");
                for (int i = 0; i < docLibraryList.getLength(); i++) {
                    Element docLibrary = (Element) docLibraryList.item(i);

                    // Check if library exists already in merge document
                    for (int j = 0; j < mDocLibraryList.getLength(); j++) {
                        if (((Element) mDocLibraryList.item(j)).getAttribute("name").equals(((Element) docLibraryList.item(i)).getAttribute("name"))) {
                            exists = true;
                            Element mDocLibrary = (Element) mDocLibraryList.item(j);

                            // Handle merging of packages in similar libraries
                            boolean packageExists = false;
                            NodeList mDocPackageList = mDocLibrary.getElementsByTagName("package");
                            if (mDocPackageList.getLength() > 0) {
                                mDocPackagesNode = mDocPackageList.item(0).getParentNode();
                            }
                            else {
                                mDocPackagesNode = mDoc.createElement("packages");
                            }
                            NodeList docPackageList = ((Element) docLibraryList.item(i)).getElementsByTagName("package");
                            for (int k = 0; k < docPackageList.getLength(); k++) {
                                for (int m = 0; m < mDocPackageList.getLength(); m++) {
                                    if (((Element) mDocPackageList.item(m)).getAttribute("name").equals(
                                            ((Element) docPackageList.item(i)).getAttribute("name"))) {
                                        packageExists = true;
                                    }
                                }
                                // If the package does not exist in the
                                // merge document's library, add it
                                if (!packageExists) {
                                    mDocPackagesNode.appendChild(mDoc.importNode(docPackageList.item(i), true));
                                }
                                // Reset 'packageExists' flag
                                packageExists = false;
                            }

                            // Handle merging of symbols in similar libraries
                            boolean symbolExists = false;
                            NodeList mDocSymbolList = mDocLibrary.getElementsByTagName("symbol");
                            if (mDocSymbolList.getLength() > 0) {
                                mDocSymbolsNode = mDocSymbolList.item(0).getParentNode();
                            }
                            else {
                                mDocSymbolsNode = mDoc.createElement("symbols");
                            }
                            NodeList docSymbolList = docLibrary.getElementsByTagName("symbol");
                            for (int k = 0; k < docSymbolList.getLength(); k++) {
                                for (int m = 0; m < mDocSymbolList.getLength(); m++) {
                                    if (((Element) mDocSymbolList.item(m)).getAttribute("name").equals(
                                            ((Element) docSymbolList.item(k)).getAttribute("name"))) {
                                        symbolExists = true;
                                    }
                                }

                                // If the symbol does not exist in the merge
                                // document's library, add it
                                if (!symbolExists) {
                                    mDocSymbolsNode.appendChild(mDoc.importNode(docSymbolList.item(k), true));
                                }
                                // Reset 'symbolExists' flag
                                symbolExists = false;
                            }

                            // Handle merging of devicesets in similar libraries
                            boolean devsetExists = false;
                            NodeList mDocDeviceSetList = mDocLibrary.getElementsByTagName("deviceset");
                            if (mDocDeviceSetList.getLength() > 0) {
                                mDocDeviceSetsNode = mDocDeviceSetList.item(0).getParentNode();
                            }
                            else {
                                mDocDeviceSetsNode = mDoc.createElement("devicesets");
                            }
                            NodeList docDeviceSetList = docLibrary.getElementsByTagName("deviceset");
                            for (int k = 0; k < docDeviceSetList.getLength(); k++) {
                                for (int m = 0; m < mDocDeviceSetList.getLength(); m++) {
                                    if (((Element) mDocDeviceSetList.item(m)).getAttribute("name").equals(
                                            ((Element) docDeviceSetList.item(k)).getAttribute("name"))) {
                                        devsetExists = true;
                                    }
                                }
                                // If the symbol does not exist in the merge
                                // document's library, add it
                                if (!devsetExists) {
                                    mDocDeviceSetsNode.appendChild(mDoc.importNode(docDeviceSetList.item(k), true));
                                }
                                // Reset 'symbolExists' flag
                                devsetExists = false;
                            }
                        }
                        // If the library does not exist in the merge document,
                        // add it
                        if (!exists) {
                            mDocLibrariesNode.appendChild(mDoc.importNode(docLibraryList.item(i), true));
                        }
                        // Reset 'exists' flag
                        exists = false;
                    }
                }

                // Handle merging of parts
                boolean partExists = false;
                NodeList docPartList = doc.getElementsByTagName("part");
                for (int p = 0; p < docPartList.getLength(); p++) {
                    // TODO merge parts (update renamed part in sheet using new
                    // name)
                    for (int m = 0; m < mDocPartList.getLength(); m++) {
                        if (((Element) mDocPartList.item(m)).getAttribute("name").equals(((Element) docPartList.item(p)).getAttribute("name"))) {
                            partExists = true;

                            // TODO pick unique name for part to avoid
                            // conflicts, try again if there is still a conflict
                            // with the new name
                            // Find last non-number, if there is a number after
                            // it, increment that number, if there is no number,
                            // append '1' to the name, check if this new name is
                            // unique
                        }
                    }

                    // If the symbol does not exist in the merge
                    // document's library, add it
                    if (!partExists) {
                        mDocPartsNode.appendChild(mDoc.importNode(docPartList.item(p), true));
                    }

                    // Reset 'symbolExists' flag
                    partExists = false;
                }

                // Handle net renaming
                // TODO Handle case where net names in source schematic and
                // destination schematic have not been renamed (ie. pins have
                // not been connected), but the two nets have matching names. In
                // this case, one of the nets needs to be renamed to something
                // unique to prevent an "accidental" connection.
                NodeList docNetList = doc.getElementsByTagName("net");
                for (int p = 0; p < docNetList.getLength(); p++) {
                    String newNetName = sch.nets.get(((Element) docNetList.item(p)).getAttribute("name"));
                    if (newNetName != null) {
                        ((Element) docNetList.item(p)).setAttribute("name", newNetName);
                    }
                }

                // Handle merging of sheets
                NodeList mDocSheetList = mDoc.getElementsByTagName("sheet");
                if (mDocSheetList.getLength() > 0) {
                    mDocSheetsNode = mDocSheetList.item(0).getParentNode();
                }
                else {
                    mDocSheetsNode = mDoc.createElement("sheets");
                }
                NodeList docSheetList = doc.getElementsByTagName("sheet");
                for (int p = 0; p < docSheetList.getLength(); p++) {
                    mDocSheetsNode.appendChild(mDoc.importNode(docSheetList.item(p), true));
                }
            }

            System.out.println(mDoc.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new EagleSchematic(mergedSch);
    }

    /**
     * @param schematicNetMap
     *            Map of schematics to the net (in that schematic) which is to
     *            be renamed.
     * @param newName
     *            Name to which the nets in the map should be renamed.
     */
    public static void connect(Map<EagleSchematic, String> schematicNetMap, String newName) {
        for (Entry<EagleSchematic, String> e : schematicNetMap.entrySet()) {
            e.getKey().setNet(e.getValue(), newName);
        }
    }

    public static Circuit buildCircuitFromSchematic(EagleSchematic sch) {
        Circuit c = new Circuit(sch.getName());
        c.setSchematic(sch);
        for (String net : sch.getNets().keySet()) {
            Pin p = Pin.getPinFromString(net, c);
            p.setNet(net);
            c.addPin(p);
        }
        return c;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public EagleSchematic clone() {
        return new EagleSchematic(schematic);
    }

}
