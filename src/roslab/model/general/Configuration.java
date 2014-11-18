/**
 *
 */
package roslab.model.general;

import java.util.ArrayList;
import java.util.List;

import roslab.model.ui.UILink;
import roslab.model.ui.UINode;

/**
 * @author Peter Gebhard
 */
public class Configuration {

    String name;
    List<UINode> nodes;
    List<UILink> links;

    /**
     * @param nodes
     * @param links
     */
    public Configuration(String name, List<UINode> nodes, List<UILink> links) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name input.");
        }
        else {
            this.name = name;
        }
        if (nodes == null) {
            this.nodes = new ArrayList<UINode>();
        }
        else {
            this.nodes = nodes;
        }
        if (links == null) {
            this.links = new ArrayList<UILink>();
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

    public boolean addUINode(UINode node) {
        return nodes.add(node);
    }

    public boolean removeUINode(UINode node) {
        return nodes.remove(node);
    }

    /**
     * @return the UI nodes
     */
    public List<UINode> getUINodes() {
        return nodes;
    }

    /**
     * @param nodes
     *            the UI nodes to set
     */
    public void setUINodes(List<UINode> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the nodes
     */
    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();

        for (UINode n : this.nodes) {
            nodes.add(n.getNode());
        }

        return nodes;
    }

    public boolean addUILink(UILink link) {
        return links.add(link);
    }

    public boolean removeUILink(UILink link) {
        return links.remove(link);
    }

    /**
     * @return the UI links
     */
    public List<UILink> getUILinks() {
        return links;
    }

    /**
     * @param links
     *            the UI links to set
     */
    public void setUILinks(List<UILink> links) {
        this.links = links;
    }

    /**
     * @return the links
     */
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<Link>();

        for (UILink l : this.links) {
            links.add(l.getLink());
        }

        return links;
    }

    /**
     * @return the links that include Node n
     */
    public List<Link> getLinks(Node n) {
        List<Link> links = new ArrayList<Link>();

        for (UILink l : this.links) {
            if (l.getSrc().getParentNode().equals(n) || l.getDest().getParentNode().equals(n)) {
                links.add(l.getLink());
            }
        }

        return links;
    }

    /**
     * @return the links that include Node n
     */
    public List<UILink> getUILinksOfType(Class<? extends Endpoint> clazz) {
        List<UILink> links = new ArrayList<UILink>();

        for (UILink l : this.links) {
            if (l.getLink().getSrc().getClass().equals(clazz) || l.getLink().getDest().getClass().equals(clazz)) {
                links.add(l);
            }
        }

        return links;
    }

}
