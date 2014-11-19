/**
 *
 */
package roslab.model.software;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class ROSNode extends Node {

    ROSNode spec;

    /**
     * @param name
     * @param spec
     */
    public ROSNode(String name, ROSNode spec) {
        super(name, spec.getPorts(), spec.getAnnotations());
        if (!this.annotations.containsKey("Rate")) {
            this.annotations.put("Rate", "50"); // Set default ROS rate to 50Hz
        }
        this.spec = spec;
    }

    /**
     * @param name
     */
    public ROSNode(String name) {
        super(name, new HashMap<String, ROSPort>(), new HashMap<String, String>());
        this.annotations.put("Rate", "50"); // Set default ROS rate to 50Hz
    }

    public ROSNode getSpec() {
        return spec;
    }

    /**
     * @return the rate
     */
    public int getRate() {
        return Integer.parseInt(this.annotations.get("Rate"));
    }

    /**
     * @param rate
     *            the rate to set
     */
    public void setRate(int rate) {
        this.annotations.put("Rate", Integer.toString(rate));
    }

    @SuppressWarnings("unchecked")
    public void addPort(ROSPort p) {
        ((Map<String, ROSPort>) features).put(p.getName(), p);
    }

    public ROSPort getPort(String name) {
        return (ROSPort) features.get(name);
    }

    @SuppressWarnings("unchecked")
    public Map<String, ROSPort> getPorts() {
        return (Map<String, ROSPort>) features;
    }

    public Map<String, ROSPort> getPublisherPorts() {
        Map<String, ROSPort> publishers = new HashMap<String, ROSPort>();
        @SuppressWarnings("unchecked")
        Collection<ROSPort> ports = (Collection<ROSPort>) features.values();
        for (ROSPort p : ports) {
            if (!p.direction) {
                publishers.put(p.getName(), p);
            }
        }
        return publishers;
    }

    public Map<String, ROSPort> getSubscriberPorts() {
        Map<String, ROSPort> subscribers = new HashMap<String, ROSPort>();
        @SuppressWarnings("unchecked")
        Collection<ROSPort> ports = (Collection<ROSPort>) features.values();
        for (ROSPort p : ports) {
            if (p.direction) {
                subscribers.put(p.getName(), p);
            }
        }
        return subscribers;
    }

}
