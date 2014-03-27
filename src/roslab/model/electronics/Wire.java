/**
 * 
 */
package roslab.model.electronics;

/**
 * @author Peter Gebhard
 *
 */
public class Wire {
	
	String name;
	Pin src;
	Pin dest;

	/**
	 * @param name
	 * @param src
	 * @param dest
	 */
	public Wire(String name, Pin src, Pin dest) {
		this.name = name;
		this.src = src;
		this.dest = dest;
	}

}
