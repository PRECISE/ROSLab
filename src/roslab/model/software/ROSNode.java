/**
 *
 */
package roslab.model.software;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class ROSNode extends Node {

    ROSNode spec;

    /**
     * Construct a new ROSNode
     *
     * @param name
     */
    public ROSNode(String name) {
        super(name, new HashMap<String, ROSPort>(), new HashMap<String, String>());
        this.annotations.put("Rate", "50"); // Set default ROS rate to 50Hz
    }

    /**
     * Construct a new ROSNode based on input spec ROSNode
     *
     * @param name
     * @param spec
     */
    public ROSNode(String name, ROSNode spec) {
        super(name, new HashMap<String, ROSPort>(), spec.getAnnotationsCopy());
        if (!this.annotations.containsKey("Rate")) {
            this.annotations.put("Rate", "50"); // Set default ROS rate to 50Hz
        }
        this.spec = spec;
        this.features = this.spec.getPortsCopy(this);
    }

    @Override
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

    public Map<String, ROSPort> getPortsCopy(ROSNode rosNode) {
        Map<String, ROSPort> copy = new HashMap<String, ROSPort>();
        for (Entry<String, ? extends Feature> e : features.entrySet()) {
            copy.put(e.getKey(), ((ROSPort) e.getValue()).getClone(e.getKey(), rosNode));
        }
        return copy;
    }

    public Map<String, ROSPort> getPublisherPorts() {
        Map<String, ROSPort> publishers = new HashMap<String, ROSPort>();
        @SuppressWarnings("unchecked")
        Collection<ROSPort> ports = (Collection<ROSPort>) features.values();
        for (ROSPort p : ports) {
            if (!p.isSubscriber()) {
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
            if (p.isSubscriber()) {
                subscribers.put(p.getName(), p);
            }
        }
        return subscribers;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        ArrayList<Endpoint> list = new ArrayList<Endpoint>();
        list.addAll(getPorts().values());
        return list;
    }

    @Override
    public Node clone(String name) {
        return new ROSNode(name, this);
    }

}
