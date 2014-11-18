/**
 *
 */
package roslab.model.general;

import java.util.HashMap;
import java.util.Map;

import roslab.model.ui.UINode;

/**
 * @author shaz
 */
public class Node {

    protected String name;
    protected Map<String, ? extends Feature> features;
    protected Map<String, String> annotations;
    protected UINode uiNode;

    /**
     * @param name
     * @param feature
     * @param annotations
     */
    public Node(String name, Map<String, ? extends Feature> features, Map<String, String> annotations) {
        // TODO: Use StringProperty and other <x>Property types for better
        // integration in GUI?
        if (name == null) {
            throw new IllegalArgumentException("Bad name.");
        }
        else {
            this.name = name;
        }
        this.features = features;
        if (annotations == null) {
            this.annotations = new HashMap<String, String>();
        }
        else {
            this.annotations = annotations;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the features
     */
    public Map<String, ? extends Feature> getFeatures() {
        return features;
    }

    /**
     * @param features
     *            the features to set
     */
    public void setFeatures(Map<String, ? extends Feature> features) {
        this.features = features;
    }

    /**
     * @param name
     *            the name of the feature to get
     * @return the feature
     */
    public Feature getFeature(String name) {
        return this.features.get(name);
    }

    /**
     * @return the annotations
     */
    public Map<String, String> getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations
     *            the annotations to set
     */
    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    /**
     * @return the annotation
     */
    public String getAnnotation(String s) {
        return annotations.get(s);
    }

    /**
     * @param annotations
     *            the annotations to set
     */
    public void addAnnotation(String key, String value) {
        this.annotations.put(key, value);
    }

    /**
     * @return the uiNode
     */
    public UINode getUINode() {
        return uiNode;
    }

    /**
     * @param uiNode
     *            the uiNode to set
     */
    public void setUINode(UINode uiNode) {
        this.uiNode = uiNode;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
