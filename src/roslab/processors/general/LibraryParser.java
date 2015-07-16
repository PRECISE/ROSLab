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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import roslab.model.general.Library;
import roslab.model.general.Node;
import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSTopic;

/**
 * @author Peter Gebhard
 */
public class LibraryParser {
    private static Yaml yaml;

    public static Library parseLibraryYAML(File libraryFile) {
        yaml = new Yaml();

        Map<String, Object> yam = new HashMap<String, Object>();

        try {
            yam = (Map<String, Object>) yaml.load(Files.newBufferedReader(libraryFile.toPath()));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        List<Node> nodes = new ArrayList<Node>();

        for (Map<String, Object> d : (List<Map<String, Object>>) yam.get("nodes")) {
            switch ((String) d.get("node_type")) {
                case "ROS":
                    ROSNode n = new ROSNode((String) d.get("name"));
                    if (d.get("custom") != null && (boolean) d.get("custom")) {
                        n.setCustomFlag(true);
                    }
                    for (Map<String, Object> t : (List<Map<String, Object>>) d.get("topics")) {
                        n.addPort(new ROSPort((String) t.get("name"), n, new ROSTopic((String) t.get("name"), new ROSMsgType((String) t
                                .get("msg_type")), "sub".equals(t.get("direction"))), false, false));
                    }
                    nodes.add(n);
                    break;
                case "HW":
                    // TODO
                    break;
                case "Elec":
                    // TODO
                    break;
            }
        }

        return new Library((String) yam.get("name"), nodes);
    }

    public static String emitLibraryYAML(Library lib) {
        // Set up YAML with Block-style dump option
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);

        // Start making YAML object, set the name element
        Map<String, Object> yam = new HashMap<String, Object>();
        yam.put("name", lib.getName());

        // Build the nodes element
        List<Object> nodes = new ArrayList<Object>();
        for (Node n : lib.getNodes()) {
            // Build each node
            Map<String, Object> node = new HashMap<String, Object>();
            node.put("name", n.getName());
            if (n instanceof ROSNode) {
                node.put("node_type", "ROS");
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
}
