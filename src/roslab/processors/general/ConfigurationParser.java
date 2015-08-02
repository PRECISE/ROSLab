/**
 * This class is used to parse and emit Configuration YAML files for
 * (de)serialization purposes.
 */
package roslab.processors.general;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import roslab.model.electronics.Circuit;
import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Link;
import roslab.model.general.Node;
import roslab.model.mechanics.HWBlock;
import roslab.model.software.ROSNode;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;

/**
 * @author Peter Gebhard
 */
public class ConfigurationParser {
    private static Yaml yaml;

    @SuppressWarnings("unchecked")
    public static Configuration parseConfigurationYAML(String configYAMLStr, String libraryYAMLStr) {
        return parseConfigurationYAML((Map<String, Object>) new Yaml().load(configYAMLStr), LibraryParser.parseLibraryYAML(libraryYAMLStr));
    }

    @SuppressWarnings("unchecked")
    public static Configuration parseConfigurationYAML(String configYAMLStr, Library lib) {
        return parseConfigurationYAML((Map<String, Object>) new Yaml().load(configYAMLStr), lib);
    }

    @SuppressWarnings("unchecked")
    public static Configuration parseConfigurationYAML(File configFile) {
        yaml = new Yaml();

        Map<String, Object> yam = new HashMap<String, Object>();

        try {
            yam = (Map<String, Object>) yaml.load(Files.newBufferedReader(configFile.toPath()));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Library lib = LibraryParser.parseLibraryYAML(Paths.get(configFile.getParent() + "/" + ((String) yam.get("library")) + ".yaml").toFile());

        return parseConfigurationYAML(yam, lib);
    }

    @SuppressWarnings("unchecked")
    public static Library parseRequiredLibrary(File configFile) {
        yaml = new Yaml();

        Map<String, Object> yam = new HashMap<String, Object>();

        try {
            yam = (Map<String, Object>) yaml.load(Files.newBufferedReader(configFile.toPath()));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return LibraryParser.parseLibraryYAML(Paths.get(configFile.getParent() + "/" + ((String) yam.get("library")) + ".yaml").toFile());
    }

    @SuppressWarnings("unchecked")
    private static Configuration parseConfigurationYAML(Map<String, Object> yam, Library lib) {

        List<Node> nodes = new ArrayList<Node>();
        List<Link> links = new ArrayList<Link>();

        for (Map<String, Object> node : (List<Map<String, Object>>) yam.get("nodes")) {
            Node n = null;
            switch ((String) node.get("node_type")) {
                case "ROSNode":
                    n = new ROSNode((String) node.get("name"), (ROSNode) lib.getNode((String) node.get("spec")));
                    break;
                case "Circuit":
                    n = new Circuit((String) node.get("name"), (Circuit) lib.getNode((String) node.get("spec")));
                    break;
                case "HWBlock":
                    n = new HWBlock((String) node.get("name"), (HWBlock) lib.getNode((String) node.get("spec")));
                    break;
            }
            n.setUINode(new UINode(n, Double.parseDouble(node.get("x").toString()), Double.parseDouble(node.get("y").toString())));
            nodes.add(n);
        }

        for (Map<String, Object> link : (List<Map<String, Object>>) yam.get("links")) {
            Node src = null;
            Node dest = null;
            for (Node n : nodes) {
                if (n.getName().equals(link.get("src_parent"))) {
                    src = n;
                }
                if (n.getName().equals(link.get("dest_parent"))) {
                    dest = n;
                }
            }
            // Throw an exception if either src or dest are not set.
            if (src == null || dest == null) {
                throw new IllegalStateException();
            }
            Link l = new Link(src.getEndpoint((String) link.get("src_name")), dest.getEndpoint((String) link.get("dest_name")));
            l.setUILink(new UILink(l));
            links.add(l);
        }

        return new Configuration((String) yam.get("name"), lib, nodes, links);
    }

    public static String emitConfigurationYAML(Configuration config) {
        // Set up YAML with Block-style dump option
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);

        // Start making YAML object, set the name element
        Map<String, Object> yam = new HashMap<String, Object>();
        yam.put("name", config.getName());
        yam.put("library", config.getLibrary().getName());

        // Build the nodes element
        List<Object> nodes = new ArrayList<Object>();
        for (Node n : config.getNodes()) {
            // Build each node
            Map<String, Object> node = new HashMap<String, Object>();
            node.put("name", n.getName());
            node.put("node_type", n.getClass().getSimpleName());
            node.put("spec", n.getSpec().getName());
            node.put("x", n.getUINode().getX());
            node.put("y", n.getUINode().getY());
            nodes.add(node);
        }
        yam.put("nodes", nodes);

        // Build the links element
        List<Object> links = new ArrayList<Object>();
        for (Link l : config.getLinks()) {
            // Build each link
            Map<String, Object> link = new HashMap<String, Object>();
            link.put("src_name", l.getSrc().getName());
            link.put("src_parent", l.getSrc().getParent().getName());
            link.put("dest_name", l.getDest().getName());
            link.put("dest_parent", l.getDest().getParent().getName());
            links.add(link);
        }
        yam.put("links", links);

        return yaml.dump(yam);
    }

    public static void emitConfigurationYAML(Configuration config, File out) {
        // Initialize FileWriter
        FileWriter writer = null;
        try {
            writer = new FileWriter(out);
            writer.write(ConfigurationParser.emitConfigurationYAML(config));
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
