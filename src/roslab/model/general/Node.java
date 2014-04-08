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
	protected Set<Annotation> annotations;
	
	/**
	 * @param name
	 * @param feature
	 * @param annotations
	 */
	public Node(String name, Map<String, ? extends Feature> features,
			Set<Annotation> annotations) {
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
	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
