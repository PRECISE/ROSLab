/**
 * 
 */
package roslab.model.ui;

import java.util.Collection;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> FETCH_HEAD

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
<<<<<<< HEAD
	List<Link> links;
=======
	Link link;
>>>>>>> FETCH_HEAD

	/**
	 * @param name
	 * @param src
	 * @param dest
	 * @param link
	 */
<<<<<<< HEAD
	public UILink(String name, UIEndpoint src, UIEndpoint dest, List<Link> links) {
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.links = links;
=======
	public UILink(String name, UIEndpoint src, UIEndpoint dest, Link link) {
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.link = link;
>>>>>>> FETCH_HEAD
	}

	/**
	 * @param name
	 * @param src
	 * @param dest
	 * @param link
	 * @param elements
	 */
<<<<<<< HEAD
	public UILink(String name, UIEndpoint src, UIEndpoint dest, List<Link> links, PathElement... elements) {
=======
	public UILink(String name, UIEndpoint src, UIEndpoint dest, Link link, PathElement... elements) {
>>>>>>> FETCH_HEAD
		super(elements);
		this.name = name;
		this.src = src;
		this.dest = dest;
<<<<<<< HEAD
		this.links = links;
=======
		this.link = link;
>>>>>>> FETCH_HEAD
	}

	/**
	 * @param name
	 * @param src
	 * @param dest
	 * @param link
	 * @param elements
	 */
<<<<<<< HEAD
	public UILink(String name, UIEndpoint src, UIEndpoint dest, List<Link> links, Collection<? extends PathElement> elements) {
=======
	public UILink(String name, UIEndpoint src, UIEndpoint dest, Link link, Collection<? extends PathElement> elements) {
>>>>>>> FETCH_HEAD
		super(elements);
		this.name = name;
		this.src = src;
		this.dest = dest;
<<<<<<< HEAD
		this.links = links;
=======
		this.link = link;
>>>>>>> FETCH_HEAD
	}

}
