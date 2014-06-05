/**
 * 
 */
package roslab.ui;

import roslab.model.general.Feature;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 *
 */
public class ROSLabLibraryItem {
	
	Node node;
	Feature feature;
	
	/**
	 * 
	 */
	public ROSLabLibraryItem() {
		
	}
	
	public boolean isNode() {
		return node != null;
	}
	
	public boolean isFeature() {
		return feature != null;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
}
