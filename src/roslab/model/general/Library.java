/**
 *
 */
package roslab.model.general;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import roslab.processors.electronics.EagleSchematic;
import roslab.processors.general.LibraryParser;

/**
 * @author Peter Gebhard
 */
public class Library {

    String name;
    List<Node> nodes;

    /**
     *
     */
    public Library() {
        this("Default", new ArrayList<Node>());
    }

    /**
     * @param nodes
     */
    public Library(List<Node> nodes) {
        this("Default", nodes);
    }

    /**
     * @param name
     */
    public Library(String name) {
        this(name, new ArrayList<Node>());
    }

    /**
     * @param name
     * @param nodes
     */
    public Library(String name, List<Node> nodes) {
        this.name = name;
        this.nodes = nodes;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * @return the specified node
     */
    public Node getNode(String name) {
        for (Node n : nodes) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    /**
     * @return the nodes
     */
    public List<Node> getNodesOfClass(Class<?> clazz) {
        List<Node> subset = new ArrayList<Node>();
        for (Node n : nodes) {
            if (n.getClass().equals(clazz)) {
                subset.add(n);
            }
        }
        return subset;
    }

    /**
     * @param nodes
     *            the nodes to set
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * @param n
     *            the node to add
     * @return
     */
    public boolean addNode(Node n) {
        if (!nodes.contains(n)) {
            return nodes.add(n);
        }
        return false;
    }

    /**
     * @param n
     *            the node to remove
     * @return
     */
    public boolean removeNode(Node n) {
        return nodes.remove(n);
    }

    public void loadElectronics() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("resources", "electronics_lib"))) {
            for (Path entry : stream) {
                if (entry.getFileName().toString().endsWith(".sch")) {
                    addNode(EagleSchematic.buildCircuitFromSchematic(new EagleSchematic(entry.toFile())));
                }
            }
        }
        catch (IOException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
    }

    /**
     * Clear the library contents and load nodes from input platform.
     *
     * @param platformName
     *            the name of the platform to load
     */
    public void loadPlatform(String platformName) {
        Library lib = LibraryParser.parseLibraryYAML(Paths.get("resources", "platforms", platformName + ".yaml").toFile());
        this.name = lib.name;
        this.nodes.clear();
        for (Node n : lib.nodes) {
            addNode(n);
        }
    }

    /**
     * Clear the library contents and load nodes from input Library file.
     *
     * @param libraryFile
     *            the name of the Library file to load
     */
    public void loadLibrary(File libraryFile) {
        Library lib = LibraryParser.parseLibraryYAML(libraryFile);
        this.name = lib.name;
        this.nodes.clear();
        for (Node n : lib.nodes) {
            addNode(n);
        }
    }
}
