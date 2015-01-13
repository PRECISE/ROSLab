/**
 *
 */
package roslab.model.electronics;

import java.util.ArrayList;
import java.util.List;

import roslab.model.general.Endpoint;
import roslab.model.general.Link;

/**
 * @author Peter Gebhard
 */
public class WireBundle extends Link {

    List<Wire> wires = new ArrayList<Wire>();

    /**
     * @param src
     * @param dest
     * @param wires
     */
    public WireBundle(Endpoint src, Endpoint dest) {
        super(src, dest);
    }

    /**
     * @param src
     * @param dest
     * @param wires
     */
    public WireBundle(Endpoint src, Endpoint dest, List<Wire> wires) {
        super(src, dest);
        this.wires = wires;
    }

    public boolean addWire(Wire w) {
        return wires.add(w);
    }

    public boolean removeWire(Wire w) {
        return wires.remove(w);
    }

}
