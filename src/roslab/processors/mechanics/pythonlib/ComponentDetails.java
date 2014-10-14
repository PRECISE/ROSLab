package roslab.processors.mechanics.pythonlib;

import java.util.List;
import java.util.Map;

public class ComponentDetails {
    // private String name;
    private String object;
    private Map<String, List<String>> parameters;

    /**
     * @param object
     * @param parameters
     */
    public ComponentDetails(String object, Map<String, List<String>> parameters) {
        // this.setName(name);
        this.object = object;
        this.parameters = parameters;
    }

    // public String getName() {
    // return name;
    // }
    //
    // public void setName(String name) {
    // this.name = name;
    // }

    /**
     * @return the object
     */
    public String getObject() {
        return object;
    }

    /**
     * @param object
     *            the object to set
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     * @return the parameters
     */
    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    /**
     * @param parameters
     *            the parameters to set
     */
    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }
}
