package roslab.ui;

import javafx.scene.control.TreeItem;
import roslab.model.general.Configuration;
import roslab.model.general.Library;
import roslab.model.general.Node;
import roslab.model.ui.UILink;
import roslab.model.ui.UINode;

public class ROSLabTree extends TreeItem<String> {

    Library library;
    Configuration config;
    TreeItem<String> libraryTree = new TreeItem<String>("Library");
    TreeItem<String> configTree = new TreeItem<String>("Configuration");
    TreeItem<String> configNodesTree = new TreeItem<String>("Nodes");
    TreeItem<String> configLinksTree = new TreeItem<String>("Links");

    public ROSLabTree(Library lib, Configuration conf) {
        library = lib;
        config = conf;
        boolean nodeAdded;

        // Add library nodes
        for (Node n : library.getNodes()) {
            nodeAdded = false;
            for (TreeItem<String> s : libraryTree.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    TreeItem<String> newNode = new TreeItem<String>(n.getName());
                    for (String f : n.getFeatures().keySet()) {
                        newNode.getChildren().add(new TreeItem<String>(f));
                    }
                    s.getChildren().add(newNode);
                    nodeAdded = true;
                }
            }
            if (!nodeAdded) {
                TreeItem<String> newItem = new TreeItem<String>(n.getClass().getSimpleName());
                TreeItem<String> newNode = new TreeItem<String>(n.getName());
                for (String f : n.getFeatures().keySet()) {
                    newNode.getChildren().add(new TreeItem<String>(f));
                }
                newItem.getChildren().add(newNode);
                libraryTree.getChildren().add(newItem);
                nodeAdded = true;
            }
        }

        // Add Configuration nodes
        for (Node n : config.getNodes()) {
            nodeAdded = false;
            for (TreeItem<String> s : configNodesTree.getChildren()) {
                if (n.getClass().getSimpleName().equals(s.getValue())) {
                    s.getChildren().add(new TreeItem<String>(n.getName()));
                    nodeAdded = true;
                }
            }
            if (!nodeAdded) {
                TreeItem<String> newItem = new TreeItem<String>(n.getClass().getSimpleName());
                newItem.getChildren().add(new TreeItem<String>(n.getName()));
                configNodesTree.getChildren().add(newItem);
                nodeAdded = true;
            }
        }
        configTree.getChildren().add(configNodesTree);

        // Add Configuration links
        for (UILink l : config.getUILinks()) {
            addConfigLink(l);
        }
        configTree.getChildren().add(configLinksTree);

        // Add to top-level tree
        getChildren().addAll(libraryTree, configTree);
    }

    public void addLibraryNode(Node n) {
        library.addNode(n);

        boolean nodeAdded = false;
        for (TreeItem<String> s : libraryTree.getChildren()) {
            if (n.getClass().getSimpleName().equals(s.getValue())) {
                s.getChildren().add(new TreeItem<String>(n.getName()));
                nodeAdded = true;
            }
        }
        if (!nodeAdded) {
            TreeItem<String> newItem = new TreeItem<String>(n.getClass().getSimpleName());
            newItem.getChildren().add(new TreeItem<String>(n.getName()));
            libraryTree.getChildren().add(newItem);
            nodeAdded = true;
        }
    }

    public void addConfigNode(UINode n) {
        boolean nodeAdded = false;
        for (TreeItem<String> s : configNodesTree.getChildren()) {
            if (n.getNode().getClass().getSimpleName().equals(s.getValue())) {
                s.getChildren().add(new TreeItem<String>(n.getName()));
                nodeAdded = true;
            }
        }
        if (!nodeAdded) {
            TreeItem<String> newItem = new TreeItem<String>(n.getNode().getClass().getSimpleName());
            newItem.getChildren().add(new TreeItem<String>(n.getName()));
            configNodesTree.getChildren().add(newItem);
            nodeAdded = true;
        }
    }

    public void addConfigLink(UILink l) {
        configLinksTree.getChildren().add(new TreeItem<String>(l.getName()));
    }
}
