/**
 * 
 */
package roslab.model.general;

/**
 * @author shaz
 *
 */
public class Link {

	String name;
	Endpoint src;
	Endpoint dest;
	
	/**
	 * @param name
	 * @param src
	 * @param dest
	 */
	public Link(String name, Endpoint src, Endpoint dest) {
		this.name = name;
		this.src = src;
		this.dest = dest;
	}
	
}
