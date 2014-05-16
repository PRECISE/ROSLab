/**
 * 
 */
package roslab.model.general;

import java.util.Map;

/**
 * @author shaz
 *
 */
public class Feature {
	
	String name;
	Node parent;
	Map<String, String> annotations;
	
	/**
	 * @param name
	 * @param parent
	 * @param annotations
	 */
	public Feature(String name, Node parent, Map<String, String> annotations) {
		this.name = name;
		this.parent = parent;
		this.annotations = annotations;
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
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Node parent) {
		this.parent = parent;
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

}
