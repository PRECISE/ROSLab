/**
 *
 */
package roslab.model.mechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Link;
import roslab.model.ui.UIEndpoint;

/**
 * @author Peter Gebhard
 */
public class Joint extends Feature implements Endpoint {

    boolean fanIn;
    boolean fanOut;

    List<Link> links = new ArrayList<Link>();

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

    @Override
    public void disconnect(Link l) {
        links.remove(l);
    }

    @Override
    public List<Link> getLinks() {
        return links;
    }

    @Override
    public UIEndpoint getUIEndpoint() {
        return this.getParent().getUINode().getUIEndpoint(this);
    }

    @Override
    public boolean isInput() {
        // TODO Maintain input/output notion for Joints?
        return true;
    }

    public Joint getClone(String name, HWBlock parent) {
        return new Joint(name, parent, this.getAnnotationsCopy(), fanIn, fanIn);
    }

}
