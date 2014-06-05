/**
 * 
 */
package roslab.ui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;

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
	public ROSLabTreeCell() {
		super();
	}
}
