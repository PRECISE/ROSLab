/**
 * 
 */
package roslab.model.ui;

import java.util.Collection;
import java.util.List;

import roslab.model.general.Link;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 * @author Peter Gebhard
 *
 */
public class UILink extends Path {
	
	String name;
	UIEndpoint src;
	UIEndpoint dest;
	List<Link> links;

	/**
	 * @param name
	 * @param src
	 * @param dest
	 * @param link
	 */
	public UILink(String name, UIEndpoint src, UIEndpoint dest, List<Link> links) {
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.links = links;
	}

	/**
	 * @param name
	 * @param src
	 * @param dest
	 * @param link
	 * @param elements
	 */
	public UILink(String name, UIEndpoint src, UIEndpoint dest, List<Link> links, PathElement... elements) {
		super(elements);
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.links = links;
	}

	/**
	 * @param name
	 * @param src
	 * @param dest
	 * @param link
	 * @param elements
	 */
	public UILink(String name, UIEndpoint src, UIEndpoint dest, List<Link> links, Collection<? extends PathElement> elements) {
		super(elements);
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.links = links;
	}

}