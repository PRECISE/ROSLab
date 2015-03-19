/**
 *
 */
package roslab.model.software;

import java.util.Map;

import roslab.model.general.Feature;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class ROSTask extends Feature {

    /**
     * @param name
     * @param parent
     * @param annotations
     */
    public ROSTask(String name, Node parent, Map<String, String> annotations) {
        super(name, parent, annotations);
    }

}
