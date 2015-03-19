/**
 *
 */
package roslab.processors.mechanics.pythonlib;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shaz
 */
public class Interface {
    private String name;
    private Map<String, String> parameters;

    /**
     * @param name
     * @param parameters
     */
    public Interface(String name, Map<String, String> parameters) {
        this.setName(name);
        this.parameters = parameters;
    }

    /**
     * @param name
     * @param parameters
     */
    public Interface(String name) {
        this.setName(name);
        this.parameters = new HashMap<String, String>();
        this.parameters.put("interface", null);
        this.parameters.put("subcomponent", null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
