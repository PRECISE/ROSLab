/**
 *
 */
package roslab.processors.electronics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import roslab.model.electronics.Circuit;
import roslab.model.electronics.Pin;
import roslab.model.electronics.Wire;
import roslab.model.electronics.WireBundle;
import roslab.model.general.Link;

/**
 * Parse EAGLE Schematics (XML)
 *
 * @author Peter Gebhard
 */
public class EagleSchematic {
    static Logger logger = LoggerFactory.getLogger(EagleSchematic.class);

    private File schematic = null;

    // Map original net names to connected net names (map value is null if that
    // net is unconnected)
    private Map<String, String> nets = new HashMap<String, String>();
    private List<String> requiredNets = new ArrayList<String>();

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

            logger.debug("Schematic : " + this.schematic.getName());

            NodeList nList = doc.getElementsByTagName("net");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Element nNode = (Element) nList.item(temp);

                // Find required pins
                NodeList pinrefList = nNode.getElementsByTagName("pinref");
                for (int pr = 0; pr < pinrefList.getLength(); pr++) {
                    Element prNode = (Element) pinrefList.item(pr);
                    if (prNode.getAttribute("pin").equals("BLOCK_REQUIREMENT")) {
                        requiredNets.add(nNode.getAttribute("name"));
                        logger.debug("  Required Net: " + nNode.getAttribute("name"));
                    }
                }

                nets.put(nNode.getAttribute("name"), null);

                logger.debug("  Net: " + nNode.getAttribute("name"));
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

    private List<String> getRequiredNets() {
        return requiredNets;
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
            throw new IllegalArgumentException("Cannot merge schematics if the input list contains less than 2.");
        }

        File mergedSch = new File(schematics.get(0).getSchematicFile().getParentFile().getParentFile().getAbsolutePath() + File.separatorChar
                + "merged_output" + File.separatorChar + filename);

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

            NodeList mDocLayers = mDoc.getElementsByTagName("layers");
            NodeList mDocLayerList = mDoc.getElementsByTagName("layer");
            if (mDocLayers.getLength() > 0) {
                mDocLayersNode = mDocLayers.item(0);
            }
            else {
                mDocLayersNode = mDoc.createElement("layers");
            }

            NodeList mDocLibraries = mDoc.getElementsByTagName("libraries");
            NodeList mDocLibraryList = mDoc.getElementsByTagName("library");
            if (mDocLibraries.getLength() > 0) {
                mDocLibrariesNode = mDocLibraries.item(0);
            }
            else {
                mDocLibrariesNode = mDoc.createElement("libraries");
            }

            NodeList mDocParts = mDoc.getElementsByTagName("parts");
            NodeList mDocPartList = mDoc.getElementsByTagName("part");
            if (mDocParts.getLength() > 0) {
                mDocPartsNode = mDocParts.item(0);
            }
            else {
                mDocPartsNode = mDoc.createElement("parts");
            }

            // Do merging for each schematic
            for (EagleSchematic sch : schematics) {
                // Skip merging of the largest schematic, since the merging
                // output schematic is the same thing (we chose to start with
                // the largest schematic)!
                if (sch.schematic.equals(largestSch)) {
                    // Handle renaming of nets for merge schematic
                    NodeList mDocNetList = mDoc.getElementsByTagName("net");
                    for (int p = 0; p < mDocNetList.getLength(); p++) {
                        Element mDocNet = (Element) mDocNetList.item(p);
                        logger.debug("Net mDoc: " + mDocNet.getAttribute("name"));
                        String newNetName = sch.nets.get(mDocNet.getAttribute("name"));
                        if (newNetName != null) {
                            mDocNet.setAttribute("name", newNetName);
                        }
                    }

                    // Skip the rest of the merging and go to the next schematic
                    continue;
                }

                // Parse the schematic into a Document
                Document doc = dBuilder.parse(sch.schematic);
                doc.getDocumentElement().normalize();
                logger.debug("Schematic: " + sch.getName());

                // An 'exists' flag used when checking if an Element already
                // exists in the merge document
                boolean exists = false;

                // Handle layers
                NodeList docLayerList = doc.getElementsByTagName("layer");
                for (int i = 0; i < docLayerList.getLength(); i++) {
                    Element docLayer = (Element) docLayerList.item(i);
                    // logger.debug("Layer doc:  [" + i + " / " +
                    // docLayerList.getLength() + " ] " +
                    // docLayer.getAttribute("name"));

                    // Check if layer exists already in merge document
                    for (int j = 0; j < mDocLayerList.getLength(); j++) {
                        Element mDocLayer = (Element) mDocLayerList.item(j);
                        // logger.debug("Layer mDoc:  [" + j + " / " +
                        // mDocLayerList.getLength() + " ] " +
                        // mDocLayer.getAttribute("name"));
                        if (mDocLayer.getAttribute("name").equals(docLayer.getAttribute("name"))) {
                            exists = true;
                            // logger.debug("Layer EXISTS");
                            break;
                        }
                    }

                    // If the layer does not exist in the merge document, add it
                    if (!exists) {
                        logger.debug("Layer doc DOES NOT exist: " + docLayer.getAttribute("name"));

                        // Find a unique layer number if it isn't already
                        int newNum = Integer.parseInt(docLayer.getAttribute("number"));
                        while (!isUniqueLayerNum(mDocLayerList, newNum) || !isUniqueLayerNum(docLayerList, newNum)) {
                            newNum++;
                        }
                        docLayer.setAttribute("number", Integer.toString(newNum));

                        // Add the layer to the layers node
                        mDocLayersNode.appendChild(mDoc.importNode(docLayer, true));
                    }

                    // Reset 'exists' flag
                    exists = false;
                }

                // Handle libraries
                NodeList docLibraryList = doc.getElementsByTagName("library");
                for (int i = 0; i < docLibraryList.getLength(); i++) {
                    Element docLibrary = (Element) docLibraryList.item(i);
                    logger.debug("Library doc: [" + i + " / " + docLibraryList.getLength() + " ] " + docLibrary.getAttribute("name"));

                    // Check if library exists already in merge document
                    for (int j = 0; j < mDocLibraryList.getLength(); j++) {
                        Element mDocLibrary = (Element) mDocLibraryList.item(j);
                        logger.debug("Library mDoc: [" + j + " / " + mDocLibraryList.getLength() + " ] " + mDocLibrary.getAttribute("name"));
                        if (mDocLibrary.getAttribute("name").equals(docLibrary.getAttribute("name"))) {
                            exists = true;

                            // Handle merging of packages in similar libraries
                            boolean packageExists = false;
                            NodeList mDocPackagesList = mDocLibrary.getElementsByTagName("packages");
                            NodeList mDocPackageList = mDocLibrary.getElementsByTagName("package");
                            if (mDocPackagesList.getLength() > 0) {
                                mDocPackagesNode = mDocPackagesList.item(0);
                            }
                            else {
                                mDocPackagesNode = mDoc.createElement("packages");
                            }
                            NodeList docPackageList = docLibrary.getElementsByTagName("package");
                            for (int k = 0; k < docPackageList.getLength(); k++) {
                                Element docPackage = (Element) docPackageList.item(k);
                                logger.debug("Package doc:  [" + k + " / " + docPackageList.getLength() + " ] " + docPackage.getAttribute("name"));
                                for (int m = 0; m < mDocPackageList.getLength(); m++) {
                                    Element mDocPackage = (Element) mDocPackageList.item(m);
                                    logger.debug("Package mDoc:  [" + m + " / " + mDocPackageList.getLength() + " ] "
                                            + mDocPackage.getAttribute("name"));
                                    if (mDocPackage.getAttribute("name").equals(docPackage.getAttribute("name"))) {
                                        logger.debug("Package doc DOES exist: " + docPackage.getAttribute("name"));
                                        packageExists = true;
                                        break;
                                    }
                                }
                                // If the package does not exist in the
                                // merge document's library, add it
                                if (!packageExists) {
                                    logger.debug("Package doc does NOT exist: " + docPackage.getAttribute("name"));
                                    mDocPackagesNode.appendChild(mDoc.importNode(docPackage, true));
                                }
                                // Reset 'packageExists' flag
                                packageExists = false;
                            }

                            // Handle merging of symbols in similar libraries
                            boolean symbolExists = false;
                            NodeList mDocSymbols = mDocLibrary.getElementsByTagName("symbols");
                            NodeList mDocSymbolList = mDocLibrary.getElementsByTagName("symbol");
                            if (mDocSymbols.getLength() > 0) {
                                mDocSymbolsNode = mDocSymbols.item(0);
                            }
                            else {
                                mDocSymbolsNode = mDoc.createElement("symbols");
                            }
                            NodeList docSymbolList = docLibrary.getElementsByTagName("symbol");
                            for (int k = 0; k < docSymbolList.getLength(); k++) {
                                Element docSymbol = (Element) docSymbolList.item(k);
                                logger.debug("Symbol doc: [" + k + " / " + docSymbolList.getLength() + " ] " + docSymbol.getAttribute("name"));
                                for (int m = 0; m < mDocSymbolList.getLength(); m++) {
                                    Element mDocSymbol = (Element) mDocSymbolList.item(m);
                                    logger.debug("Symbol mDoc: [" + m + " / " + mDocSymbolList.getLength() + " ] " + mDocSymbol.getAttribute("name"));
                                    if (mDocSymbol.getAttribute("name").equals(docSymbol.getAttribute("name"))) {
                                        symbolExists = true;
                                        break;
                                    }
                                }

                                // If the symbol does not exist in the merge
                                // document's library, add it
                                if (!symbolExists) {
                                    logger.debug("Symbol doc does NOT exist: " + docSymbol.getAttribute("name"));
                                    mDocSymbolsNode.appendChild(mDoc.importNode(docSymbol, true));
                                }

                                // Reset 'symbolExists' flag
                                symbolExists = false;
                            }

                            // Handle merging of devicesets in similar libraries
                            boolean devsetExists = false;
                            NodeList mDocDeviceSets = mDocLibrary.getElementsByTagName("devicesets");
                            NodeList mDocDeviceSetList = mDocLibrary.getElementsByTagName("deviceset");
                            if (mDocDeviceSets.getLength() > 0) {
                                mDocDeviceSetsNode = mDocDeviceSets.item(0);
                            }
                            else {
                                mDocDeviceSetsNode = mDoc.createElement("devicesets");
                            }
                            NodeList docDeviceSetList = docLibrary.getElementsByTagName("deviceset");
                            for (int k = 0; k < docDeviceSetList.getLength(); k++) {
                                Element docDeviceSet = (Element) docDeviceSetList.item(k);
                                logger.debug("DeviceSet doc: [" + k + " / " + docDeviceSetList.getLength() + " ] "
                                        + docDeviceSet.getAttribute("name"));
                                for (int m = 0; m < mDocDeviceSetList.getLength(); m++) {
                                    Element mDocDeviceSet = (Element) mDocDeviceSetList.item(m);
                                    logger.debug("DeviceSet mDoc: [" + m + " / " + mDocDeviceSetList.getLength() + " ] "
                                            + mDocDeviceSet.getAttribute("name"));
                                    if (mDocDeviceSet.getAttribute("name").equals(docDeviceSet.getAttribute("name"))) {
                                        devsetExists = true;
                                        break;
                                    }
                                }

                                // If the symbol does not exist in the merge
                                // document's library, add it
                                if (!devsetExists) {
                                    logger.debug("DeviceSet doc does NOT exist: " + docDeviceSet.getAttribute("name"));
                                    mDocDeviceSetsNode.appendChild(mDoc.importNode(docDeviceSet, true));
                                }

                                // Reset 'devsetExists' flag
                                devsetExists = false;
                            }

                            // Break out of the loop if we found the library
                            // already exists.
                            break;
                        }
                    }

                    // If the library does not exist in the merge document,
                    // add it
                    if (!exists) {
                        mDocLibrariesNode.appendChild(mDoc.importNode(docLibrary, true));
                    }
                    // Reset 'exists' flag
                    exists = false;
                }

                // Handle merging of parts
                NodeList docPartList = doc.getElementsByTagName("part");
                for (int p = 0; p < docPartList.getLength(); p++) {
                    Element docPart = (Element) docPartList.item(p);
                    logger.debug("Part doc: [" + p + " / " + docPartList.getLength() + " ] " + docPart.getAttribute("name"));
                    for (int m = 0; m < mDocPartList.getLength(); m++) {
                        Element mDocPart = (Element) mDocPartList.item(m);
                        logger.debug("Part mDoc: [" + m + " / " + mDocPartList.getLength() + " ] " + mDocPart.getAttribute("name"));
                        if (mDocPart.getAttribute("name").equals(docPart.getAttribute("name"))) {
                            // If there is a name conflict, pick a unique name
                            // for part. Try again if there is still a conflict
                            // with the new name.
                            String newName = makeUniqueName(docPart.getAttribute("name"));
                            while (!isUniqueName(mDocPartList, newName) || !isUniqueName(docPartList, newName)) {
                                newName = makeUniqueName(newName);
                            }

                            // Update all references to the old part name in
                            // pinref and instance tags
                            NodeList docPinrefList = doc.getElementsByTagName("pinref");
                            for (int pinref = 0; pinref < docPinrefList.getLength(); pinref++) {
                                Element docPinref = (Element) docPinrefList.item(pinref);
                                if (docPinref.getAttribute("part").equals(docPart.getAttribute("name"))) {
                                    logger.debug("Pinref doc: [" + pinref + " / " + docPinrefList.getLength() + " ] "
                                            + docPinref.getAttribute("part"));
                                    docPinref.setAttribute("part", newName);
                                }
                            }
                            NodeList docInstanceList = doc.getElementsByTagName("instance");
                            for (int inst = 0; inst < docInstanceList.getLength(); inst++) {
                                Element docInst = (Element) docInstanceList.item(inst);
                                if (docInst.getAttribute("part").equals(docPart.getAttribute("name"))) {
                                    logger.debug("Instance doc: [" + inst + " / " + docInstanceList.getLength() + " ] "
                                            + docInst.getAttribute("part"));
                                    docInst.setAttribute("part", newName);
                                }
                            }

                            docPart.setAttribute("name", newName);
                        }
                    }

                    // Merge the part from the original document to the new
                    // merge document
                    mDocPartsNode.appendChild(mDoc.importNode(docPart, true));
                }

                // Handle net renaming
                // TODO Handle case where net names in source schematic and
                // destination schematic have not been renamed (ie. pins have
                // not been connected), but the two nets have matching names. In
                // this case, one of the nets needs to be renamed to something
                // unique to prevent an "accidental" connection.
                NodeList docNetList = doc.getElementsByTagName("net");
                for (int p = 0; p < docNetList.getLength(); p++) {
                    Element docNet = (Element) docNetList.item(p);
                    logger.debug("Net doc: [" + p + " / " + docNetList.getLength() + " ] " + docNet.getAttribute("name"));
                    String newNetName = sch.nets.get(docNet.getAttribute("name"));
                    if (newNetName != null) {
                        docNet.setAttribute("name", newNetName);
                    }
                }

                // Handle merging of sheets
                NodeList mDocSheets = mDoc.getElementsByTagName("sheets");
                if (mDocSheets.getLength() > 0) {
                    mDocSheetsNode = mDocSheets.item(0);
                }
                else {
                    mDocSheetsNode = mDoc.createElement("sheets");
                }
                NodeList docSheetList = doc.getElementsByTagName("sheet");
                for (int p = 0; p < docSheetList.getLength(); p++) {
                    Element docSheet = (Element) docSheetList.item(p);
                    logger.debug("Sheet doc: [" + p + " / " + docSheetList.getLength() + " ] " + docSheet.getAttribute("name"));
                    mDocSheetsNode.appendChild(mDoc.importNode(docSheet, true));
                }
            }

            // TODO Test out removing 'constant' attribute from 'attribute' tags
            // (Eagle shows a warning)
            //
            // NodeList mDocAttrList = mDoc.getElementsByTagName("attribute");
            // for (int at = 0; at < mDocAttrList.getLength(); at++) {
            // Element mDocAttr = (Element) mDocAttrList.item(at);
            // logger.debug("Attribute mDoc: [" + at + " / " +
            // mDocAttrList.getLength() + " ] " +
            // mDocAttr.getAttribute("attribute"));
            // mDocAttr.removeAttribute("constant");
            // }

            // Transform DOM back to Schematic file
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            Properties outFormat = new Properties();
            outFormat.setProperty(OutputKeys.INDENT, "yes");
            outFormat.setProperty(OutputKeys.METHOD, "xml");
            outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            outFormat.setProperty(OutputKeys.VERSION, "1.0");
            outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperties(outFormat);
            DOMSource domSource = new DOMSource(mDoc.getDocumentElement());
            StreamResult result = new StreamResult(new FileOutputStream(mergedSch));
            transformer.transform(domSource, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new EagleSchematic(mergedSch);
    }

    private static boolean isUniqueName(NodeList mDocPartList, String newName) {
        for (int m = 0; m < mDocPartList.getLength(); m++) {
            if (((Element) mDocPartList.item(m)).getAttribute("name").equals(newName)) {
                return false;
            }
        }
        return true;
    }

    // Find the number at the end of the input and increment it.
    // If there is no number, append '1' to the input.
    private static String makeUniqueName(String name) {
        // Try to find the entire number at the end of the string
        int i = name.length();
        while (i > 0 && Character.isDigit(name.charAt(i - 1))) {
            i--;
        }

        // Handle case where there is no number at the end of the input string
        if (i == name.length()) {
            return name.concat("1");
        }

        // Get integer from ending number in the string
        int num = Integer.parseInt(name.substring(i));

        // Increment the ending number
        num++;

        // Replace the ending number with the incremented value
        return name.substring(0, i).concat(String.valueOf(num));
    }

    private static boolean isUniqueLayerNum(NodeList mDocList, int newNum) {
        for (int m = 0; m < mDocList.getLength(); m++) {
            if (((Element) mDocList.item(m)).getAttribute("number").equals(String.valueOf(newNum))) {
                return false;
            }
        }
        return true;
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

    public static void connectWires(List<Link> links) {
        for (Link wb : links) {
            for (Wire w : ((WireBundle) wb).getWires()) {
                Map<EagleSchematic, String> schematicNetMap = new HashMap<EagleSchematic, String>();
                schematicNetMap.put(((Circuit) w.getSrc().getParent()).getSchematic(), w.getSrc().getNet());
                schematicNetMap.put(((Circuit) w.getDest().getParent()).getSchematic(), w.getDest().getNet());
                EagleSchematic.connect(schematicNetMap, w.getName());
            }
        }
    }

    public static Circuit buildCircuitFromSchematic(EagleSchematic sch) {
        Circuit c = new Circuit(sch.getName());
        c.setSchematic(sch);
        for (String net : sch.getNets().keySet()) {
            Pin p = Pin.getPinFromString(net, c);
            p.setNet(net);
            p.setRequired(sch.getRequiredNets().contains(net));
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EagleSchematic [" + (schematic != null ? "schematic=" + schematic : "") + "]";
    }

}
