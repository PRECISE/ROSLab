/**
 * 
 */
package roslab.model.hardware;

import java.util.Map;
import java.util.Set;

import roslab.model.general.Annotation;
import roslab.model.general.Node;

/**
 * @author shaz
 *
 */
public class HWBlock extends Node {
	
	HWBlock spec;
	HWBlockType type;

	/**
	 * @param name
	 * @param joints
	 * @param annotations
	 * @param spec
	 */
	public HWBlock(String name, Map<String, Joint> joints,
			Set<Annotation> annotations, HWBlock spec, HWBlockType type) {
		super(name, joints, annotations);
		this.spec = spec;
		this.type = type;
	}

	/**
	 * @return the spec
	 */
	public HWBlock getSpec() {
		return spec;
	}

	/**
	 * @param spec the spec to set
	 */
	public void setSpec(HWBlock spec) {
		this.spec = spec;
	}

	/**
	 * @return the type
	 */
	public HWBlockType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(HWBlockType type) {
		this.type = type;
	}

}
