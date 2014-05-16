/**
 * 
 */
package roslab.model.general;

import java.util.Map;
import java.util.Set;

/**
 * @author shaz
 *
 */
public class Node {
	
	protected String name;
	protected Map<String, ? extends Feature> features;
	protected Map<String, String> annotations;
	
	/**
	 * @param name
	 * @param feature
	 * @param annotations
	 */
	public Node(String name, Map<String, ? extends Feature> features,
			Map<String, String> annotations) {
		this.name = name;
		this.features = features;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
