/**
 *
 */
package roslab.ui.general;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

/**
 * @author Peter Gebhard
 */
public final class ROSLabTreeCell<String> extends TreeCell<String> {

    ContextMenu menu = new ContextMenu();

    /**
     * @param menu
     */
    public ROSLabTreeCell() {
        super();

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

    // /**
    // * @param menu
    // */
    // public ROSLabTreeCell(Feature f) {
    // super();
    // this.item = new ROSLabLibraryItem();
    // this.item.setFeature(f);
    //
    // setOnDragDetected(new EventHandler<MouseEvent>() {
    // @Override
    // public void handle(MouseEvent event) {
    // /* drag was detected, start a drag-and-drop gesture */
    // /* allow any transfer mode */
    // Dragboard db = startDragAndDrop(TransferMode.ANY);
    //
    // /* Put a string on a dragboard */
    // ClipboardContent content = new ClipboardContent();
    // content.putString("feature:" + item.feature.getName());
    // db.setContent(content);
    //
    // event.consume();
    // }
    // });
    // }

    /**
     * @return the item
     */
    public ROSLabLibraryItem getLibraryItem() {
        return item;
    }

    /**
     * @param item
     *            the item to set
     */
    public void setLibraryItem(ROSLabLibraryItem item) {
        this.item = item;
    }

    /**
     * @return the menu
     */
    public ContextMenu getMenu() {
        return menu;
    }

    /**
     * @param menu
     *            the menu to set
     */
    public void setMenu(ContextMenu menu) {
        this.menu = menu;
    }

    public static <String> Callback<TreeView<String>, TreeCell<String>> forTreeView(ROSLabTree tree) {
        return new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> listView) {
                ROSLabTreeCell<String> cell = new ROSLabTreeCell<String>();
                return cell;
            }
        };
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        }
        else {
            setText(item == null ? "null" : item);
            setGraphic(null);
        }
    }

}
