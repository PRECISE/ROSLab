/**
 * 
 */
package roslab.ui;

import roslab.model.general.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;

/**
 * @author Peter Gebhard
 *
 */
public final class ROSLabTreeCell extends TreeCell<Node> {
	
	ContextMenu menu = new ContextMenu();

	/**
	 * @param menu
	 */
	public ROSLabTreeCell() {
		//if (this.getTreeItem().getValue())
	}
	
}
