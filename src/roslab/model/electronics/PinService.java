/**
 * 
 */
package roslab.model.electronics;

/**
 * @author shaz
 *
 */
public class PinService {
	
	String name;
	boolean optional;

	/**
	 * @param name
	 * @param optional
	 */
	public PinService(String name, boolean optional) {
		this.name = name;
		this.optional = optional;
	}

}
