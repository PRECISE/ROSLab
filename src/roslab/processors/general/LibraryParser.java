/**
 * This class is used to parse and emit Library YAML files for (de)serialization
 * purposes.
 */
package roslab.processors.general;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import roslab.model.electronics.Circuit;
import roslab.model.general.Library;
import roslab.model.general.Node;
import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSTopic;
import roslab.processors.electronics.EagleSchematic;

/**
 * @author Peter Gebhard
 */
public class LibraryParser {
    private static Yaml yaml;
    private static String LIBRARY_YAML_VERSION = "1.0";
    public static Path SW_LIBRARY_PATH = Paths.get("resources", "platforms");
    public static Path EE_LIBRARY_PATH = Paths.get("resources", "electronics_lib");
    public static Path HW_LIBRARY_PATH = Paths.get("resources", "mechanics_lib");

    @SuppressWarnings("unchecked")
    private static Library parseLibraryYAML(Map<String, Object> yam) {
        // Check if this is a valid Library file
        if (!((String) ((Map<String, Object>) yam.get("format")).get("type")).equals("Library")) {
            return null;
        }

        List<Node> nodes = new ArrayList<Node>();

        for (Map<String, Object> node : (List<Map<String, Object>>) yam.get("nodes")) {
            switch ((String) node.get("node_type")) {
                case "ROSNode":
                    ROSNode rn = new ROSNode((String) node.get("name"));
                    if (node.get("custom") != null && (boolean) node.get("custom")) {
                        rn.setCustomFlag(true);
                    }
                    for (Map<String, Object> topic : (List<Map<String, Object>>) node.get("topics")) {
                        rn.addPort(new ROSPort((String) topic.get("name"), rn, new ROSTopic((String) topic.get("name"), new ROSMsgType((String) topic
                                .get("msg_type")), "sub".equals(topic.get("direction"))), false, false));
                    }
                    nodes.add(rn);
                    break;
                case "HWBlock":
                    // TODO
                    break;
                case "Circuit":
                    nodes.add(EagleSchematic.buildCircuitFromSchematic(HW_LIBRARY_PATH.resolve((String) node.get("schematic"))));
                    break;
            }
        }

        return new Library((String) yam.get("name"), nodes);
    }

    @SuppressWarnings("unchecked")
    public static Library parseLibraryYAML(Path libraryPath) {
        yaml = new Yaml();

        Map<String, Object> yam = new HashMap<String, Object>();

        try {
            yam = (Map<String, Object>) yaml.load(Files.newBufferedReader(libraryPath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return parseLibraryYAML(yam);
    }

    @SuppressWarnings("unchecked")
    public static Library parseLibraryYAML(String libraryYAMLStr) {
        return parseLibraryYAML((Map<String, Object>) new Yaml().load(libraryYAMLStr));
    }

    public static String emitLibraryYAML(Library lib) {
        // Set up YAML with Block-style dump option
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);

        Map<String, Object> yam = new HashMap<String, Object>();

        // Set the format element
        Map<String, Object> format = new HashMap<String, Object>();
        format.put("type", "Library");
        format.put("version", LIBRARY_YAML_VERSION);
        yam.put("format", format);

        // Set the name element
        yam.put("name", lib.getName());

        // Build the nodes element
        List<Object> nodes = new ArrayList<Object>();
        for (Node n : lib.getNodes()) {
            // Build each node
            Map<String, Object> node = new HashMap<String, Object>();
            node.put("name", n.getName());
            switch (n.getClass().getSimpleName()) {
                case "ROSNode":
                    node.put("node_type", "ROSNode");
                    // Build each topic
                    List<Object> topics = new ArrayList<Object>();
                    for (ROSPort port : ((ROSNode) n).getPorts().values()) {
                        Map<String, Object> topic = new HashMap<String, Object>();
                        topic.put("name", port.getTopicName());
                        topic.put("direction", port.isSubscriber() ? "sub" : "pub");
                        topic.put("msg_type", port.getType().type);
                        topics.add(topic);
                    }
                    node.put("topics", topics);
                    break;
                case "HWBlock":
                    node.put("node_type", "HWBlock");
                    // Build each joint
                    break;
                case "Circuit":
                    node.put("node_type", "Circuit");
                    node.put("schematic", ((Circuit) n).getSchematic().getSchematicFile().getName());
                    break;
            }
            nodes.add(node);
        }
        yam.put("nodes", nodes);

        return yaml.dump(yam);
    }

    public static void emitLibraryYAML(Library lib, File out) {
        // Initialize FileWriter
        FileWriter writer = null;
        try {
            writer = new FileWriter(out);
            writer.write(LibraryParser.emitLibraryYAML(lib));
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean isValidLibraryYAML(Path yamlPath) {
        yaml = new Yaml();

        Map<String, Object> yam = new HashMap<String, Object>();

        try {
            yam = (Map<String, Object>) yaml.load(Files.newBufferedReader(yamlPath));
        }
        catch (Exception e) {
            return false;
        }

        return ((String) ((Map<String, Object>) yam.get("format")).get("type")).equals("Library");
    }
}
