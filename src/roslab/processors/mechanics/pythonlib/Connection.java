/**
 *
 */
package roslab.processors.mechanics.pythonlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author shaz
 */
public class Connection {
    private List<String> src;
    private List<String> dest;
    private ConnectionType type;
    private Map<String, String> parameters;

    /**
     * @param src
     * @param dest
     * @param type
     * @param parameters
     */
    public Connection(List<String> src, List<String> dest, ConnectionType type, Map<String, String> parameters) {
        this.src = src;
        this.dest = dest;
        this.type = type;
        this.parameters = parameters;
    }

    public Connection() {
        this.src = new ArrayList<String>();
        this.src.add("0");
        this.src.add("1");
        this.dest = new ArrayList<String>();
        this.dest.add("0");
        this.dest.add("1");
        this.type = ConnectionType.Flat;
        this.parameters = new HashMap<String, String>();
        this.parameters.put("angle", null);
        this.parameters.put("depth", null);
        this.parameters.put("name", null);
    }

    /**
     * @return the src
     */
    public List<String> getSrc() {
        return src;
    }

    /**
     * @param src
     *            the src to set
     */
    public void setSrc(List<String> src) {
        this.src = src;
    }

    /**
     * @return the dest
     */
    public List<String> getDest() {
        return dest;
    }

    /**
     * @param dest
     *            the dest to set
     */
    public void setDest(List<String> dest) {
        this.dest = dest;
    }

    /**
     * @return the type
     */
    public ConnectionType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(ConnectionType type) {
        this.type = type;
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
     * @param parameters
     *            the parameters to set
     */
    public void setParameters(String[][] parameters) {
        this.parameters.clear();
        for (String[] p : parameters) {
            this.parameters.put(p[0], p[1]);
        }
    }

    /**
     * @return the parameters
     */
    public String[][] getParametersArray() {
        String[][] out = new String[parameters.keySet().size()][2];
        int i = 0;
        for (Entry<String, String> e : parameters.entrySet()) {
            out[i][0] = e.getKey();
            out[i][1] = e.getValue();
            i++;
        }
        return out;
    }

    public void setSrcName(String name) {
        this.src.remove(0);
        this.src.add(0, name);
    }

    public void setSrcJoint(String joint) {
        this.src.remove(1);
        this.src.add(1, joint);
    }

    public void setDestName(String name) {
        this.dest.remove(0);
        this.dest.add(0, name);
    }

    public void setDestJoint(String joint) {
        this.dest.remove(1);
        this.dest.add(1, joint);
    }

    public List<Object> getGenericList() {
        List<Object> l = new ArrayList<Object>();
        l.add(src);
        l.add(dest);
        l.add(type.toString());
        l.add(parameters);
        return l;
    }
}
