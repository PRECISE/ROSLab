/**
 *
 */
package roslab.model.software;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Link;
import roslab.model.ui.UIEndpoint;
import roslab.model.ui.UILink;

/**
 * @author Peter Gebhard
 */
public class ROSPort extends Feature implements Endpoint {

    ROSPortType type;
    boolean direction; // true is in/subscribe, false is out/publish
    boolean fanIn;
    boolean fanOut;

    List<Link> links = new ArrayList<Link>();

    /**
     * @param name
     * @param parent
     * @param annotations
     * @param type
     * @param direction
     * @param fanIn
     * @param fanOut
     */
    public ROSPort(String name, ROSNode parent, Map<String, String> annotations, ROSPortType type, boolean direction, boolean fanIn, boolean fanOut) {
        super(name, parent, annotations);
        this.type = type;
        this.direction = direction;
        this.fanIn = fanIn && direction;
        this.fanOut = fanOut && !direction;
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanIn()
     */
    @Override
    public boolean isFanIn() {
        return fanIn && direction;
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanOut()
     */
    @Override
    public boolean isFanOut() {
        return fanOut && !direction;
    }

    /*
     * (non-Javadoc)
     * @see
     * roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
     */
    @Override
    public boolean canConnect(Endpoint e) {
        // Only allow ROSPorts to connect to other ROSPorts
        if (e instanceof ROSPort) {
            ROSPort p = (ROSPort) e;

            // fanIn/fanOut checks
            boolean passedFanCheck = true;
            if ((!isFanIn() && !p.direction && links.size() > 0) || (!isFanOut() && p.direction && links.size() > 0)) {
                passedFanCheck = false;
            }

            // Valid connection if directions are opposite, but types match.
            return (this.direction != p.direction) && (this.type == p.type) && passedFanCheck;
        }
        return false;
    }

    @Override
    public Link connect(Endpoint e) {
        if (e instanceof ROSPort) {
            ROSPort src;
            ROSPort dest;

            if (((ROSPort) e).direction) {
                dest = (ROSPort) e;
                src = this;
            }
            else {
                src = (ROSPort) e;
                dest = this;
            }

            Link l = new Link(src, dest);
            src.links.add(l);
            dest.links.add(l);

            UIEndpoint srcEP = src.getParent().getUINode().getUIEndpoint(src);
            UIEndpoint destEP = dest.getParent().getUINode().getUIEndpoint(dest);
            srcEP.addUILink(new UILink(l, srcEP, destEP));
            destEP.addUILink(new UILink(l, srcEP, destEP));

            return l;
        }
        return null;
    }
}
