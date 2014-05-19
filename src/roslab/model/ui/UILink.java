/**
 * 
 */
package roslab.model.ui;

import javafx.scene.shape.Path;
import roslab.model.general.Link;

/**
 * @author Peter Gebhard
 *
 */
public class UILink extends Path {

	Link link;
	UIEndpoint src;
	UIEndpoint dest;

	/**
	 * @param name
	 * @param src
	 * @param dest
	 * @param link
	 */
	public UILink(Link link, UIEndpoint src, UIEndpoint dest) {
		this.link = link;
		this.src = src;
		this.dest = dest;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.link.getName();
	}

	/**
	 * @return the src
	 */
	public UIEndpoint getSrc() {
		return src;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(UIEndpoint src) {
		this.src = src;
	}

	/**
	 * @return the dest
	 */
	public UIEndpoint getDest() {
		return dest;
	}

	/**
	 * @param dest the dest to set
	 */
	public void setDest(UIEndpoint dest) {
		this.dest = dest;
	}

	/**
	 * @return the link
	 */
	public Link getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(Link link) {
		this.link = link;
	}

}
