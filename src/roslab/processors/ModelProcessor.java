/**
 * 
 */
package roslab.processors;

import roslab.model.general.Configuration;

/**
 * @author shaz
 *
 */
public abstract class ModelProcessor {
	
	Configuration config;

	/**
	 * 
	 */
	public ModelProcessor(Configuration config) {
		this.config = config;
	}
	
	public abstract void output();

}
