/**
 *
 */
package roslab.processors.mechanics.pythonlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Peter Gebhard
 */
public class Component {
    private List<Connection> connections;
    private Map<String, Map<String, String>> interfaces;
    private Map<String, String> parameters;
    private Map<String, ComponentDetails> subcomponents;

    /**
     * @param connections
     * @param interfaces
     * @param parameters
     * @param subcomponents
     */
    public Component(List<Connection> connections, Map<String, Map<String, String>> interfaces, Map<String, String> parameters,
            Map<String, ComponentDetails> subcomponents) {
        this.connections = connections != null ? connections : new ArrayList<Connection>();
        this.interfaces = interfaces != null ? interfaces : new HashMap<String, Map<String, String>>();
        this.parameters = parameters != null ? parameters : new HashMap<String, String>();
        this.subcomponents = subcomponents != null ? subcomponents : new HashMap<String, ComponentDetails>();
    }

    /**
     * @return the connections
     */
    public List<Connection> getConnections() {
        return connections;
    }

    /**
     * @param connections
     *            the connections to set
     */
    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    /**
     * @return the interfaces
     */
    public Map<String, Map<String, String>> getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces
     *            the interfaces to set
     */
    public void setInterfaces(Map<String, Map<String, String>> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters
     *            the parameters to set
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the subcomponents
     */
    public Map<String, ComponentDetails> getSubcomponents() {
        return subcomponents;
    }

    /**
     * @param subcomponents
     *            the subcomponents to set
     */
    public void setSubcomponents(Map<String, ComponentDetails> subcomponents) {
        this.subcomponents = subcomponents;
    }

    @Override
    public Component clone() {
        return new Component(this.connections, this.interfaces, this.parameters, this.subcomponents);
    }
}
