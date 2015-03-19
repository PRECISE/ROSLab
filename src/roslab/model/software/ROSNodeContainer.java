/**
 *
 */
package roslab.model.software;

import java.util.List;

import roslab.model.general.Link;

/**
 * @author Peter Gebhard
 */
public class ROSNodeContainer extends ROSNode {

    List<ROSNode> nodes;
    List<Link> links;

    public ROSNodeContainer(String name, ROSNode spec) {
        super(name, spec);
    }

    public void addNode(ROSNode node) {
        nodes.add(node);
    }

    public void removeNode(ROSNode node) {
        nodes.remove(node);
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public void removeLink(Link link) {
        links.remove(link);
    }
}
