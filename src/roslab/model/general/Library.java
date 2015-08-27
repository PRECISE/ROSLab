/**
 *
 */
package roslab.model.general;

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
        this(null, null);
    }

    /**
     * @param nodes
     */
    public Library(List<Node> nodes) {
        this(null, nodes);
    }

    /**
     * @param name
     */
    public Library(String name) {
        this(name, null);
    }

    /**
     * @param name
     * @param nodes
     */
    public Library(String name, List<Node> nodes) {
        if (name == null) {
            this.name = "Default";
        }
        else {
            this.name = name;
        }
        if (nodes == null) {
            this.nodes = new ArrayList<Node>();
        }
        else {
            this.nodes = nodes;
        }
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

    public static Library loadBaseElectronicsLibrary() {
        Library lib = new Library("Base");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("resources", "electronics_lib"))) {
            for (Path entry : stream) {
                if (entry.getFileName().toString().endsWith(".sch")) {
                    lib.addNode(EagleSchematic.buildCircuitFromSchematic(entry));
                }
            }
        }
        catch (IOException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        return lib;
    }

    /**
     * Clear the library contents and load nodes from input Library file.
     *
     * @param libraryFile
     *            the name of the Library file to load
     */
    public void loadLibrary(Path libraryPath) {
        Library lib = LibraryParser.parseLibraryYAML(libraryPath);
        this.name = lib.name;
        this.nodes.clear();
        for (Node n : lib.nodes) {
            addNode(n);
        }
    }
}
