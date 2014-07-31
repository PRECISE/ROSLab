/**
 * 
 */
package roslab.model.general;

import java.util.Map;
import java.util.Set;

import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import roslab.model.ui.UINode;

/**
 * @author shaz
 *
 */
public class Node {
	
	protected StringProperty name;
	protected MapProperty<String, ? extends Feature> features;
	protected MapProperty<String, String> annotations;
	protected ObjectProperty<UINode> uiNode;
	
	/**
	 * @param name
	 * @param feature
	 * @param annotations
	 */
	public Node(String name, Map<String, ? extends Feature> features,
			Map<String, String> annotations) {
		this.name = new SimpleStringProperty(name);
		this.features = new SimpleMapProperty(FXCollections.observableMap(features));
		this.annotations = new SimpleMapProperty(FXCollections.observableMap(annotations));
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the features
	 */
	public Map<String, ? extends Feature> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(Map<String, ? extends Feature> features) {
		this.features = features;
	}

	/**
	 * @return the annotations
	 */
	public Map<String, String> getAnnotations() {
		return annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(Map<String, String> annotations) {
		this.annotations = annotations;
	}

	/**
	 * @return the annotations
	 */
	public String getAnnotation(String s) {
		return annotations.get(s);
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotation(String key, String value) {
		this.annotations.put(key, value);
	}

	/**
	 * @return the uiNode
	 */
	public UINode getUiNode() {
		return uiNode;
	}

	/**
	 * @param uiNode the uiNode to set
	 */
	public void setUiNode(UINode uiNode) {
		this.uiNode = uiNode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	public static void addLink(Node src, Node dest) {
		
	}

}
