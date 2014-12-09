/**
 *
 */
package roslab.ui;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import roslab.model.general.Feature;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
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

        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* drag was detected, start a drag-and-drop gesture */
                /* allow any transfer mode */
                Dragboard db = startDragAndDrop(TransferMode.ANY);

                /* Put a string on a dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString("node:" + item.node.getName());
                db.setContent(content);

                event.consume();
            }
        });
    }

    /**
     * @param menu
     */
    public ROSLabTreeCell(Feature f) {
        super();
        this.item = new ROSLabLibraryItem();
        this.item.setFeature(f);

        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* drag was detected, start a drag-and-drop gesture */
                /* allow any transfer mode */
                Dragboard db = startDragAndDrop(TransferMode.ANY);

                /* Put a string on a dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString("feature:" + item.feature.getName());
                db.setContent(content);

                event.consume();
            }
        });
    }

}
