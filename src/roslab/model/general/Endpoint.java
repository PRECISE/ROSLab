/**
 *
 */
package roslab.model.general;

/**
 * @author Peter Gebhard
 */
public interface Endpoint {

    public String getName();

    public Node getParent();

    public boolean isFanIn();

    public boolean isFanOut();

    public boolean canConnect(Endpoint e);

    public Link connect(Endpoint e);

}
