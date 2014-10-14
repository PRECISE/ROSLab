/**
 *
 */
package roslab.processors;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STRawGroupDir;

import roslab.model.general.Configuration;

/**
 * @author Peter Gebhard
 */
public abstract class ModelProcessor {

    protected Configuration config;
    protected STGroup stg = new STRawGroupDir("templates");
    protected ST st;

    /**
     *
     */
    public ModelProcessor(Configuration config) {
        this.config = config;
        stg.delimiterStartChar = '$';
        stg.delimiterStopChar = '$';
    }

    public abstract String output();

}
