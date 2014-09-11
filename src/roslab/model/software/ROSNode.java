/**
 *
 */
package roslab.model.software;

import java.util.Map;

import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class ROSNode extends Node {

    ROSNode spec;

    /**
     * @param name
     * @param feature
     * @param annotations
     * @param spec
     */
    public ROSNode(String name, Map<String, ROSPort> ports, Map<String, String> annotations, ROSNode spec) {
        super(name, ports, annotations);
        this.spec = spec;
    }

    public ROSNode getSpec() {
        return spec;
    }

    @SuppressWarnings("unchecked")
    public void addPort(ROSPort p) {
        ((Map<String, ROSPort>) this.features).put(p.getName(), p);
    }

    public ROSPort getPort(String name) {
        return (ROSPort) this.features.get(name);
    }

    @SuppressWarnings("unchecked")
    public Map<String, ROSPort> getPorts() {
        return (Map<String, ROSPort>) features;
    }

}
