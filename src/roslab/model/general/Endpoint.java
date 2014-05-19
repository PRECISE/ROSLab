/**
 * 
 */
package roslab.model.general;

/**
 * @author Peter Gebhard
 */
public interface Endpoint {
	
	public String getName();
	public boolean isFanIn();
	public boolean isFanOut();
	public boolean canConnect(Endpoint e);

}
