package roslab.ui;

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
            this.controller = cont;
            boolean nodeAdded = false;

            // Add library nodes
            for (Node n : this.controller.getLibrary().getNodes()) {
                nodeAdded = false;
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
        }

        public void addNode(Node n) {
            controller.getLibrary().addNode(n);
            boolean nodeAdded = false;
            for (TreeItem<String> s : this.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    s.getChildren().add(new NodeTreeItem(n, controller));
                    nodeAdded = true;
                }
            }
            if (!nodeAdded) {
                ContextMenuTreeItem newItem = new ContextMenuTreeItem(n.getClass().getSimpleName());
                newItem.getChildren().add(new NodeTreeItem(n, controller));
                this.getChildren().add(newItem);
                nodeAdded = true;
            }
        }

        @Override
        public ContextMenu getMenu() {
            MenuItem mItem = new MenuItem("Add User-Defined Node...");
            mItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // TODO Auto-generated method stub
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
            this.controller = cont;
            boolean nodeAdded = false;

            // Add Configuration nodes
            for (Node n : this.controller.getConfig().getNodes()) {
                nodeAdded = false;
                for (TreeItem<String> s : configNodesTree.getChildren()) {
                    if (n.getClass().getSimpleName().equals(s.getValue())) {
                        s.getChildren().add(new NodeTreeItem(n, this.controller));
                        nodeAdded = true;
                    }
                }
                if (!nodeAdded) {
                    ContextMenuTreeItem newItem = new ContextMenuTreeItem(n.getClass().getSimpleName());
                    newItem.getChildren().add(new NodeTreeItem(n, this.controller));
                    configNodesTree.getChildren().add(newItem);
                    nodeAdded = true;
                }
            }
            this.getChildren().add(configNodesTree);

            // Add Configuration links
            for (Link l : this.controller.getConfig().getLinks()) {
                nodeAdded = false;
                for (TreeItem<String> s : configLinksTree.getChildren()) {
                    if (l.getClass().getSimpleName().equals(s.getValue())) {
                        s.getChildren().add(new LinkTreeItem(l, this.controller));
                        nodeAdded = true;
                    }
                }
                if (!nodeAdded) {
                    ContextMenuTreeItem newItem = new ContextMenuTreeItem(l.getClass().getSimpleName());
                    newItem.getChildren().add(new LinkTreeItem(l, this.controller));
                    configLinksTree.getChildren().add(newItem);
                    nodeAdded = true;
                }
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
            if (!controller.getConfig().contains(n)) {
                controller.getConfig().addNode(n);
            }
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
            if (!controller.getConfig().contains(l)) {
                controller.getConfig().addLink(l);
            }
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
            if (controller.getConfig().contains(n)) {
                controller.getConfig().removeNode(n);
            }
            for (TreeItem<String> s : configNodesTree.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    for (TreeItem<String> i : s.getChildren()) {
                        if (((NodeTreeItem) i).node == n) {
                            s.getChildren().remove(i);
                        }
                    }
                    if (s.getChildren().isEmpty()) {
                        configNodesTree.getChildren().remove(s);
                    }
                }
            }
        }

        public void removeLink(Link l) {
            if (controller.getConfig().contains(l)) {
                controller.getConfig().removeLink(l);
            }
            for (TreeItem<String> s : configLinksTree.getChildren()) {
                if (l.getClass().getSimpleName().equals(s.getValue())) {
                    for (TreeItem<String> i : s.getChildren()) {
                        if (((LinkTreeItem) i).link == l) {
                            s.getChildren().remove(i);
                        }
                    }
                    if (s.getChildren().isEmpty()) {
                        configLinksTree.getChildren().remove(s);
                    }
                }
            }
        }

        @Override
        public ContextMenu getMenu() {
            MenuItem mItem = new MenuItem("Generate single-system code");
            mItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // TODO Auto-generated method stub
                }
            });
            MenuItem m2Item = new MenuItem("Generate multi-system container and code...");
            m2Item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // TODO Pop up external interface dialog
                }
            });
            return new ContextMenu(mItem, m2Item);
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
            MenuItem mItem = null;

            if (this.node instanceof ROSNode) {
                mItem = new MenuItem("Add New Port...");
                mItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // TODO Auto-generated method stub
                    }
                });
            }

            MenuItem m2Item = new MenuItem("Delete");
            m2Item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    controller.removeConfigNode(node);
                }
            });

            return new ContextMenu(mItem, m2Item);
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
