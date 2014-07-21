/**
 * 
 */
package roslab.ui;

import roslab.model.general.Feature;
import roslab.model.general.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 * @author Peter Gebhard
 *
 */
public final class ROSLabTreeCell extends TreeCell<String> {
	
	ROSLabLibraryItem item;
	ContextMenu menu = new ContextMenu();
	
	/**
	 * @param menu
	 */
	public ROSLabTreeCell(Node n) {
		super();
		this.item = new ROSLabLibraryItem();
		this.item.setNode(n);
		this.startEdit();
		this.commitEdit(n.getName());
	}
	
	/**
	 * @param menu
	 */
	public ROSLabTreeCell(Feature f) {
		super();
		this.item = new ROSLabLibraryItem();
		this.item.setFeature(f);
		this.startEdit();
		this.commitEdit(f.getName());
	}
}
