/**
 * 
 */
package roslab.model.hardware;

import java.util.Map;

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
			Map<String, String> annotations, HWBlock spec, HWBlockType type) {
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
