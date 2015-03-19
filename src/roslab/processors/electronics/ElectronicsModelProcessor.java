/**
 *
 */
package roslab.processors.electronics;

import roslab.model.general.Configuration;
import roslab.processors.ModelProcessor;

/**
 * @author shaz
 */
public class ElectronicsModelProcessor extends ModelProcessor {

    /**
     * @param config
     */
    public ElectronicsModelProcessor(Configuration config) {
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
