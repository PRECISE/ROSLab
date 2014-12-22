/**
 *
 */
package roslab.model.general;

import java.util.Map;

/**
 * @author Peter Gebhard
 */
public interface Endpoint {

    public String getName();

    public Node getParent();

    public Map<String, String> getAnnotations();

    public boolean isInput();

    public boolean isFanIn();

    public boolean isFanOut();

    public boolean canConnect(Endpoint e);

    public Link connect(Endpoint e);

}
