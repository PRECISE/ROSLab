package roslab.ui.general;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import roslab.ROSLabController;
import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Link;
import roslab.model.general.Node;
import roslab.model.software.ROSNode;
import roslab.processors.software.ROSNodeCodeGenerator;

public class ROSLabTree extends TreeItem<String> {
    public class ContextMenuTreeItem extends TreeItem<String> {
        public ContextMenu menu = new ContextMenu();

        public ContextMenuTreeItem(String title) {
            super(title);
        }

        public ContextMenu getMenu() {
            return this.menu;
        }

        public void setMenu(ContextMenu menu) {
            this.menu = menu;
        }
    }

    public class LibraryTreeItem extends ContextMenuTreeItem {
        private ROSLabController controller;

        public LibraryTreeItem(ROSLabController cont) {
            super("Library (" + cont.getLibrary().getName() + ")");
            controller = cont;

            // Add library nodes
            for (Node n : controller.getLibrary().getNodes()) {
                addNode(n);
            }
        }

        public void addNode(Node n) {
            controller.getLibrary().addNode(n);
            boolean nodeAdded = false;
            for (TreeItem<String> s : this.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    NodeTreeItem newNode = new NodeTreeItem(n, this.controller);
                    for (String f : n.getFeatures().keySet()) {
                        newNode.getChildren().add(new ContextMenuTreeItem(f));
                    }
                    s.getChildren().add(newNode);
                    nodeAdded = true;
                }
            }
            if (!nodeAdded) {
                ContextMenuTreeItem newItem = new ContextMenuTreeItem(n.getClass().getSimpleName());
                NodeTreeItem newNode = new NodeTreeItem(n, this.controller);
                for (String f : n.getFeatures().keySet()) {
                    newNode.getChildren().add(new ContextMenuTreeItem(f));
                }
                newItem.getChildren().add(newNode);
                this.getChildren().add(newItem);
                nodeAdded = true;
            }
        }

        public void removeNode(Node n) {
            controller.getLibrary().removeNode(n);
            int removeIndex2 = -1;
            for (TreeItem<String> s : this.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    int removeIndex = -1;
                    for (TreeItem<String> i : s.getChildren()) {
                        if (((NodeTreeItem) i).node == n) {
                            removeIndex = s.getChildren().indexOf(i);
                        }
                    }
                    if (removeIndex != -1) {
                        s.getChildren().remove(removeIndex);
                    }
                    if (s.getChildren().isEmpty()) {
                        removeIndex2 = this.getChildren().indexOf(s);
                    }
                }
            }
            if (removeIndex2 != -1) {
                this.getChildren().remove(removeIndex2);
            }
        }

        @Override
        public ContextMenu getMenu() {
            MenuItem mItem = new MenuItem("Add User-Defined Node...");
            mItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    controller.showNewUserDefinedDialog();
                }
            });
            return new ContextMenu(mItem);
        }
    }

    public class ConfigTreeItem extends ContextMenuTreeItem {
        private ROSLabController controller;
        private ContextMenuTreeItem configNodesTree = new ContextMenuTreeItem("Nodes");
        private ContextMenuTreeItem configLinksTree = new ContextMenuTreeItem("Links");

        public ConfigTreeItem(ROSLabController cont) {
            super("Configuration (" + cont.getConfig().getName() + ")");
            controller = cont;

            // Add Configuration nodes
            for (Node n : controller.getConfig().getNodes()) {
                addNode(n);
            }
            this.getChildren().add(configNodesTree);

            // Add Configuration links
            for (Link l : controller.getConfig().getLinks()) {
                addLink(l);
            }
            this.getChildren().add(configLinksTree);

            MenuItem nItem = new MenuItem("Add node...");
            nItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    controller.showNewNodeDialog();
                }
            });
            configNodesTree.setMenu(new ContextMenu(nItem));

            MenuItem mItem = new MenuItem("Add link...");
            mItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    controller.showNewLinkDialog();
                }
            });
            configLinksTree.setMenu(new ContextMenu(mItem));
        }

        public void addNode(Node n) {
            controller.getConfig().addNode(n);
            boolean nodeAdded = false;
            for (TreeItem<String> s : configNodesTree.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    s.getChildren().add(new NodeTreeItem(n, controller));
                    nodeAdded = true;
                }
            }
            if (!nodeAdded) {
                ContextMenuTreeItem newItem = new ContextMenuTreeItem(n.getClass().getSimpleName());
                newItem.getChildren().add(new NodeTreeItem(n, controller));
                configNodesTree.getChildren().add(newItem);
                nodeAdded = true;
            }
        }

        public void addLink(Link l) {
            controller.getConfig().addLink(l);
            boolean nodeAdded = false;
            for (TreeItem<String> s : configLinksTree.getChildren()) {
                if (l.getClass().getSimpleName().equals(s.getValue())) {
                    s.getChildren().add(new LinkTreeItem(l, controller));
                    nodeAdded = true;
                }
            }
            if (!nodeAdded) {
                ContextMenuTreeItem newItem = new ContextMenuTreeItem(l.getClass().getSimpleName());
                newItem.getChildren().add(new LinkTreeItem(l, controller));
                configLinksTree.getChildren().add(newItem);
                nodeAdded = true;
            }
        }

        public void removeNode(Node n) {
            controller.getConfig().removeNode(n);
            int removeIndex2 = -1;
            for (TreeItem<String> s : configNodesTree.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    int removeIndex = -1;
                    for (TreeItem<String> i : s.getChildren()) {
                        if (((NodeTreeItem) i).node == n) {
                            removeIndex = s.getChildren().indexOf(i);
                        }
                    }
                    if (removeIndex != -1) {
                        s.getChildren().remove(removeIndex);
                    }
                    if (s.getChildren().isEmpty()) {
                        removeIndex2 = configNodesTree.getChildren().indexOf(s);
                    }
                }
            }
            if (removeIndex2 != -1) {
                configNodesTree.getChildren().remove(removeIndex2);
            }
        }

        public void removeLink(Link l) {
            controller.getConfig().removeLink(l);
            int removeIndex2 = -1;
            for (TreeItem<String> s : configLinksTree.getChildren()) {
                if (l.getClass().getSimpleName().equals(s.getValue())) {
                    int removeIndex = -1;
                    for (TreeItem<String> i : s.getChildren()) {
                        if (((LinkTreeItem) i).link == l) {
                            removeIndex = s.getChildren().indexOf(i);
                        }
                    }
                    if (removeIndex != -1) {
                        s.getChildren().remove(removeIndex);
                    }
                    if (s.getChildren().isEmpty()) {
                        removeIndex2 = configLinksTree.getChildren().indexOf(s);
                    }
                }
            }
            if (removeIndex2 != -1) {
                configLinksTree.getChildren().remove(removeIndex2);
            }
        }

        @Override
        public ContextMenu getMenu() {
            MenuItem mItem = new MenuItem("Generate single-system code");
            mItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    for (Node n : controller.getConfig().getNodesOfType(ROSNode.class)) {
                        try {
                            if (n.getAnnotation("user-defined") != null && n.getAnnotation("user-defined").equals("true")) {
                                ROSNodeCodeGenerator.buildNode((ROSNode) n, new File(n.getName() + ".cpp"));
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            MenuItem m2Item = new MenuItem("Generate multi-system container and code...");
            m2Item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // TODO Pop up external interface dialog
                }
            });
            return new ContextMenu(mItem);
        }
    }

    public class NodeTreeItem extends ContextMenuTreeItem {
        private Node node;
        private ROSLabController controller;

        public NodeTreeItem(Node node, ROSLabController controller) {
            super(node.getName());
            this.node = node;
            this.controller = controller;
        }

        @Override
        public ContextMenu getMenu() {
            // Return empty menu if node is under Library tree and not a
            // user-defined node
            if (this.getParent().getParent() instanceof LibraryTreeItem) {
                if (node.getAnnotation("user-defined") == "true") {
                    MenuItem mItem = null;

                    if (this.node instanceof ROSNode) {
                        mItem = new MenuItem("Add New Port...");
                        mItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                controller.showNewPortDialog(node);
                            }
                        });
                    }
                    return new ContextMenu(mItem);
                }

                return new ContextMenu();
            }

            MenuItem m2Item = new MenuItem("Delete");
            m2Item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    controller.removeConfigNode(node);
                }
            });

            return new ContextMenu(m2Item);
        }
    }

    public class LinkTreeItem extends ContextMenuTreeItem {
        private Link link;
        private ROSLabController controller;

        public LinkTreeItem(Link link, ROSLabController controller) {
            super(link.getName());
            this.link = link;
            this.controller = controller;
        }
    }

    public final class TreeCellImpl extends TreeCell<String> {

        // setOnDragDetected(new EventHandler<MouseEvent>() {
        // @Override
        // public void handle(MouseEvent event) {
        // /* drag was detected, start a drag-and-drop gesture */
        // /* allow any transfer mode */
        // Dragboard db = startDragAndDrop(TransferMode.ANY);
        //
        // /* Put a string on a dragboard */
        // ClipboardContent content = new ClipboardContent();
        // content.putString("node:" + item.node.getName());
        // db.setContent(content);
        //
        // event.consume();
        // }
        // });

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            }
            else {
                setText(getItem() == null ? "" : getItem().toString());
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(((ContextMenuTreeItem) getTreeItem()).getMenu());
            }
        }
    }

    LibraryTreeItem libraryTree;
    ConfigTreeItem configTree;

    @SuppressWarnings("unchecked")
    public ROSLabTree(Library lib, Configuration conf, ROSLabController controller) {
        libraryTree = new LibraryTreeItem(controller);
        configTree = new ConfigTreeItem(controller);

        // Add to top-level tree
        getChildren().addAll(libraryTree, configTree);
    }

    public void addLibraryNode(Node n) {
        libraryTree.addNode(n);
    }

    public void removeLibraryNode(Node n) {
        libraryTree.removeNode(n);
    }

    public void addConfigNode(Node n) {
        configTree.addNode(n);
    }

    public void addConfigLink(Link l) {
        configTree.addLink(l);
    }

    public void removeConfigNode(Node n) {
        configTree.removeNode(n);
    }

    public void removeConfigLink(Link l) {
        configTree.removeLink(l);
    }
}
