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
import roslab.model.ui.UIEndpoint;
import roslab.processors.electronics.EagleSchematic;
import roslab.processors.electronics.PinMatcher;

/**
 * @author Peter Gebhard
 */
public class Circuit extends Node implements Endpoint {

    Circuit spec;
    EagleSchematic schematic;

    List<WireBundle> wireBundles = new ArrayList<WireBundle>();

    // TODO CircuitType type;
    // TODO List<CircuitResource> resources;

    /**
     * @param name
     * @param spec
     */
    public Circuit(String name, Circuit spec) {
        super(name, new HashMap<String, Pin>(), spec.getAnnotationsCopy());
        this.spec = spec;
        this.schematic = spec.schematic.clone();
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

    /**
     * @return the schematic
     */
    public EagleSchematic getSchematic() {
        return schematic;
    }

    /**
     * @param schematic
     *            the schematic to set
     */
    public void setSchematic(EagleSchematic schematic) {
        this.schematic = schematic;
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

    public Map<String, Pin> getRequiredPins() {
        Map<String, Pin> result = new HashMap<String, Pin>();

        for (Pin p : this.getPins().values()) {
            if (p.required) {
                result.put(p.getName(), p);
            }
        }

        return result;
    }

    public Map<String, Pin> getPinsCopy(Circuit c) {
        Map<String, Pin> copy = new HashMap<String, Pin>();
        for (Entry<String, ? extends Feature> e : features.entrySet()) {
            copy.put(e.getKey(), ((Pin) e.getValue()).clone(e.getKey(), c));
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
        // there is initially no requested service unused, but through some
        // reassignment of pins, we could make it work.

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
        if (e instanceof Circuit) {
            Circuit c = (Circuit) e;
            Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
            List<Pin> componentPins = getConnectedComponentPins();
            componentPins.addAll(this.getRequiredPins().values());

            // Fill the pin matching matrix
            Integer[][] matrix = new Integer[componentPins.size()][c.getRequiredPins().size()];
            int mIndex = 0;
            for (Pin p : componentPins) {
                matrix[mIndex] = generateRow(c, p);
                mIndex++;
            }

            mapping = PinMatcher.match(matrix);

            return mapping != null;
        }

        return false;
    }

    private List<Pin> getConnectedComponentPins() {
        List<Pin> result = new ArrayList<Pin>();

        for (WireBundle wb : this.wireBundles) {
            for (Wire w : wb.wires) {
                result.add(w.src);
            }
        }

        return result;
    }

    @Override
    public Link connect(Endpoint e) {
        if (e instanceof Circuit && canConnect(e)) {
            Circuit c = (Circuit) e;

            Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
            List<Pin> componentPins = getConnectedComponentPins();
            componentPins.addAll(this.getRequiredPins().values());

            // Fill the pin matching matrix
            Integer[][] matrix = new Integer[componentPins.size()][c.getRequiredPins().size()];
            int mIndex = 0;
            for (Pin p : componentPins) {
                matrix[mIndex] = generateRow(c, p);
                mIndex++;
            }

            mapping = PinMatcher.match(matrix);

            // Return false if the match did not work, ie. the component cannot
            // be connected.
            if (mapping == null) {
                return null;
            }

            WireBundle wb = new WireBundle(this, c);

            for (Entry<Integer, Integer> pinPair : mapping.entrySet()) {
                Wire w = ((Pin) componentPins.toArray()[pinPair.getKey()]).connect((Pin) c.getPins().values().toArray()[pinPair.getValue()]);
                if (w != null) {
                    wb.addWire(w);

                    // Make connection in EagleSchematics
                    Map<EagleSchematic, String> schematicNetMap = new HashMap<EagleSchematic, String>();
                    schematicNetMap.put(this.schematic, w.src.net);
                    schematicNetMap.put(c.schematic, w.dest.net);
                    EagleSchematic.connect(schematicNetMap, w.name);
                }
            }

            wireBundles.add(wb);

            return wb;
        }

        return null;
    }

    @Override
    public void disconnect(Link l) {
        wireBundles.remove(l);
        // TODO Remove wire bundle connections from EagleSchematic netMap
    }

    @Override
    public List<WireBundle> getLinks() {
        return wireBundles;
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
    public UIEndpoint getUIEndpoint() {
        return uiNode.getUIEndpoint(this);
    }

    @Override
    public boolean isInput() {
        return true;
    }

    @Override
    public Circuit clone(String name) {
        return new Circuit(name, this);
    }

    private Integer[] generateRow(Circuit c, Pin p) {
        Integer[] result = new Integer[c.getPins().size()];
        int i = 0;

        for (Pin pin : c.getPins().values()) {
            if (p.canConnect(pin)) {
                result[i] = 1;
            }
            else {
                result[i] = 0;
            }
            i++;
        }

        return result;
    }
}
