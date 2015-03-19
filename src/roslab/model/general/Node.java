/**
 *
 */
package roslab.model.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import roslab.model.ui.UINode;

/**
 * @author Peter Gebhard
 */
public abstract class Node {

    protected String name;
    protected Map<String, ? extends Feature> features;
    protected Map<String, String> annotations = new HashMap<String, String>();
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
        if (annotations != null) {
            this.annotations = annotations;
        }
    }

    /**
     * @param name
     * @param feature
     * @param annotations
     */
    public Node(String name, Map<String, ? extends Feature> features) {
        // TODO: Use StringProperty and other <x>Property types for better
        // integration in GUI?
        if (name == null) {
            throw new IllegalArgumentException("Bad name.");
        }
        else {
            this.name = name;
        }
        this.features = features;
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
     * @return the annotations
     */
    public Map<String, String> getAnnotationsCopy() {
        Map<String, String> copy = new HashMap<String, String>();
        for (Entry<String, String> e : annotations.entrySet()) {
            copy.put(e.getKey(), e.getValue());
        }
        return copy;
    }

    /**
     * @param annotations
     *            the annotations to set
     */
    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    /**
     * @param a
     *            the annotation to get
     * @return the annotation value
     */
    public String getAnnotation(String a) {
        return annotations.get(a);
    }

    /**
     * @param key
     *            the key to set
     * @param value
     *            the value to set
     */
    public void addAnnotation(String key, String value) {
        this.annotations.put(key, value);
    }

    /**
     * @param key
     *            the key to remove
     */
    public void removeAnnotation(String key) {
        this.annotations.remove(key);
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

    public abstract List<Endpoint> getEndpoints();

    public abstract Node getSpec();

    public abstract Node clone(String name);

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
