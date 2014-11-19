/**
 *
 */
package roslab.processors.software;

import roslab.model.general.Configuration;
import roslab.processors.ModelProcessor;

/**
 * @author shaz
 */
public class SoftwareModelProcessor extends ModelProcessor {

    /**
     * @param config
     */
    public SoftwareModelProcessor(Configuration config) {
        super(config);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * @see roslab.processors.ModelProcessor#output()
     */
    @Override
    public String output() {
        return st.toString();
    }

}
