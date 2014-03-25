/**
 * 
 */
package roslab.model.general;

/**
 * @author shaz
 *
 */
public class Channel {

	String name;
	Endpoint src;
	Endpoint dest;
	
	/**
	 * @param name
	 * @param src
	 * @param dest
	 */
	public Channel(String name, Endpoint src, Endpoint dest) {
		this.name = name;
		this.src = src;
		this.dest = dest;
	}
	
}
