/**
 *
 */
package roslab.model.electronics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Link;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class Circuit extends Node implements Endpoint {

    Circuit spec;

    // TODO CircuitType type;
    // TODO List<CircuitResource> resources;

    /**
     * @param name
     * @param spec
     */
    public Circuit(String name, Circuit spec) {
        super(name, new HashMap<String, Pin>(), spec.getAnnotationsCopy());
        this.spec = spec;
        this.features = spec.getPinsCopy(this);
    }

    /**
     * @param name
     * @param pins
     * @param annotations
     * @param spec
     */
    public Circuit(String name, Map<String, Pin> pins) {
        super(name, pins, new HashMap<String, String>());
    }

    /**
     * @param name
     */
    public Circuit(String name) {
        super(name, new HashMap<String, Pin>(), new HashMap<String, String>());
    }

    @Override
    public Circuit getSpec() {
        return spec;
    }

    @SuppressWarnings("unchecked")
    public void addPin(Pin p) {
        ((Map<String, Pin>) features).put(p.getName(), p);
    }

    public Pin getPin(String name) {
        return (Pin) features.get(name);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Pin> getPins() {
        return (Map<String, Pin>) features;
    }

    public Map<String, Pin> getPinsCopy(Circuit c) {
        Map<String, Pin> copy = new HashMap<String, Pin>();
        for (Entry<String, ? extends Feature> e : features.entrySet()) {
            copy.put(e.getKey(), ((Pin) e.getValue()).getClone(e.getKey(), c));
        }
        return copy;
    }

    public Pin getUnusedServiceByName(String serviceName) {
        for (Entry<String, ? extends Feature> entry : features.entrySet()) {
            Pin pin = (Pin) entry.getValue();

            // If the pin has not been assigned yet...
            if (pin.getAssignedService() == null) {
                // Look through all the pin's services to see if it has a
                // matching service
                for (PinService service : pin.getServices()) {
                    if (service.getName().equals(serviceName)) {
                        return pin;
                    }
                }
            }
        }

        // TODO: Need to add optimizing algorithm here to handle cases where
        // there is initially
        // no requested service unused, but through some reassignment of pins,
        // we could make it work.

        return null;
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanIn()
     */
    @Override
    public boolean isFanIn() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanOut()
     */
    @Override
    public boolean isFanOut() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
     */
    @Override
    public boolean canConnect(Endpoint e) {
        // Only allow ROSPorts to connect to other ROSPorts
        if (e instanceof Circuit) {
            Circuit c = (Circuit) e;
            // TODO Perform pin analysis here.
        }
        return false;
    }

    @Override
    public Link connect(Endpoint e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Node getParent() {
        return spec;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        ArrayList<Endpoint> list = new ArrayList<Endpoint>();
        list.add(this);
        return list;
    }

    @Override
    public boolean isInput() {
        return true;
    }

    @Override
    public Circuit clone(String name) {
        return new Circuit(name, this);
    }
}
