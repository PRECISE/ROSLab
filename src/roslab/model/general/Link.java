/**
 * 
 */
package roslab.model.general;

/**
 * @author shaz
 *
 */
public class Link {

	Endpoint src;
	Endpoint dest;
	
	/**
	 * @param src
	 * @param dest
	 */
	public Link(String name, Endpoint src, Endpoint dest) {
		this.src = src;
		this.dest = dest;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.src.getName() + " -- " + this.dest.getName();
	}

	/**
	 * @return the src
	 */
	public Endpoint getSrc() {
		return src;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(Endpoint src) {
		this.src = src;
	}

	/**
	 * @return the dest
	 */
	public Endpoint getDest() {
		return dest;
	}

	/**
	 * @param dest the dest to set
	 */
	public void setDest(Endpoint dest) {
		this.dest = dest;
	}
	
}
