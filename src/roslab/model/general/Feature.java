/**
 *
 */
package roslab.model.general;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Peter Gebhard
 */
public class Feature {

    protected String name;
    protected Node parent;
    protected Map<String, String> annotations = new HashMap<String, String>();

    /**
     * @param name
     * @param parent
     * @param annotations
     */
    public Feature(String name, Node parent, Map<String, String> annotations) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name input.");
        }
        else {
            this.name = name;
        }
        if (parent == null) {
            throw new IllegalArgumentException("Bad parent input.");
        }
        else {
            this.parent = parent;
        }
        if (annotations != null) {
            this.annotations = annotations;
        }
    }

    /**
     * @param name
     * @param parent
     * @param annotations
     */
    public Feature(String name, Node parent) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name input.");
        }
        else {
            this.name = name;
        }
        if (parent == null) {
            throw new IllegalArgumentException("Bad parent input.");
        }
        else {
            this.parent = parent;
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
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Node parent) {
        this.parent = parent;
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
     * @return the annotations
     */
    public String getAnnotation(String s) {
        return annotations.get(s);
    }

    /**
     * @param annotations
     *            the annotations to set
     */
    public void setAnnotation(String key, String value) {
        this.annotations.put(key, value);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Feature [" + (name != null ? "name=" + name + ", " : "") + (parent != null ? "parent=" + parent + ", " : "")
                + (annotations != null ? "annotations=" + annotations : "") + "]";
    }

}
