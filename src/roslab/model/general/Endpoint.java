/**
 *
 */
package roslab.model.general;

import java.util.List;
import java.util.Map;

import roslab.model.ui.UIEndpoint;

/**
 * @author Peter Gebhard
 */
public interface Endpoint {

    public String getName();

    public Node getParent();

    public UIEndpoint getUIEndpoint();

    public Map<String, String> getAnnotations();

    public List<? extends Link> getLinks();

    public boolean isInput();

    public boolean isFanIn();

    public boolean isFanOut();

    public boolean canConnect(Endpoint e);

    public Link connect(Endpoint e);

    public void disconnect(Link l);

}
