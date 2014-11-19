/**
 *
 */
package roslab.model.mechanics;

import java.util.Map;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Link;

/**
 * @author Peter Gebhard
 */
public class Joint extends Feature implements Endpoint {

    boolean fanIn;
    boolean fanOut;

    /**
     * @param name
     * @param parent
     * @param annotations
     * @param fanIn
     * @param fanOut
     */
    public Joint(String name, HWBlock parent, Map<String, String> annotations, boolean fanIn, boolean fanOut) {
        super(name, parent, annotations);
        this.fanIn = fanIn;
        this.fanOut = fanOut;
    }

    /**
     * @param name
     * @param parent
     * @param fanIn
     * @param fanOut
     */
    public Joint(String name, HWBlock parent, boolean fanIn, boolean fanOut) {
        super(name, parent);
        this.fanIn = fanIn;
        this.fanOut = fanOut;
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanIn()
     */
    @Override
    public boolean isFanIn() {
        return fanIn;
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanOut()
     */
    @Override
    public boolean isFanOut() {
        return fanOut;
    }

    /*
     * (non-Javadoc)
     * @see
     * roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
     */
    @Override
    public boolean canConnect(Endpoint e) {
        return true;
    }

    @Override
    public Link connect(Endpoint e) {
        // TODO Auto-generated method stub
        return null;
    }

}
