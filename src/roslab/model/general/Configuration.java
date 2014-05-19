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
 *
 */
public class Configuration {

	List<UINode> nodes;
	List<UILink> links;
	
	/**
	 * @param nodes
	 * @param links
	 */
	public Configuration(List<UINode> nodes, List<UILink> links) {
		this.nodes = nodes;
		this.links = links;
	}

	/**
	 * @return the UI nodes
	 */
	public List<UINode> getUINodes() {
		return nodes;
	}

	/**
	 * @param nodes the UI nodes to set
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

	/**
	 * @return the UI links
	 */
	public List<UILink> getUILinks() {
		return links;
	}

	/**
	 * @param links the UI links to set
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
