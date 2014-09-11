/**
 *
 */
package roslab.model.electronics;

import java.util.Map;
import java.util.Map.Entry;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
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
     * @param pins
     * @param annotations
     * @param spec
     */
    public Circuit(String name, Map<String, Pin> pins, Map<String, String> annotations, Circuit spec) {
        super(name, pins, annotations);
        this.spec = spec;
    }

    public Circuit getSpec() {
        return spec;
    }

    @SuppressWarnings("unchecked")
    public void addPin(Pin p) {
        ((Map<String, Pin>) this.features).put(p.getName(), p);
    }

    public Pin getPin(String name) {
        return (Pin) this.features.get(name);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Pin> getPins() {
        return (Map<String, Pin>) features;
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

}
