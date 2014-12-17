/**
 *
 */
package roslab.model.general;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Gebhard
 */
public class Configuration {

    String name;
    List<Node> nodes;
    List<Link> links;

    /**
     * @param nodes
     * @param links
     */
    public Configuration(String name, List<Node> nodes, List<Link> links) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name input.");
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
        if (links == null) {
            this.links = new ArrayList<Link>();
        }
        else {
            this.links = links;
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

    public boolean addNode(Node node) {
        return nodes.add(node);
    }

    public boolean removeNode(Node node) {
        return nodes.remove(node);
    }

    /**
     * @return the UI nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * @param nodes
     *            the UI nodes to set
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public boolean addLink(Link link) {
        return links.add(link);
    }

    public boolean removeLink(Link link) {
        return links.remove(link);
    }

    /**
     * @return the UI links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * @param links
     *            the UI links to set
     */
    public void setUILinks(List<Link> links) {
        this.links = links;
    }

    /**
     * @return the links that include Node n
     */
    public List<Link> getLinks(Node n) {
        List<Link> links = new ArrayList<Link>();

        for (Link l : this.links) {
            if (l.getSrc().getParent().equals(n) || l.getDest().getParent().equals(n)) {
                links.add(l);
            }
        }

        return links;
    }

    /**
     * @return the links that include Node n
     */
    public List<Endpoint> getEndpoints() {
        List<Endpoint> eps = new ArrayList<Endpoint>();

        for (Node n : this.getNodes()) {
            eps.addAll(n.getEndpoints());
        }

        return eps;
    }

    public boolean contains(Node n) {
        return this.nodes.contains(n);
    }

    public boolean contains(Link l) {
        return this.links.contains(l);
    }

}
