/**
 *
 */
package roslab.processors.electronics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parse EAGLE Schematics (XML)
 *
 * @author Rachel
 */
public class EagleSchematic {

    private List<String> nets = new ArrayList<String>();

    /**
     * Construct an EAGLE Schematic object
     *
     * @param schematicFile
     */
    public EagleSchematic(File schematicFile) {
        // TODO Parse file and store nets in nets field.
    }

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

}
