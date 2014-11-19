/**
 *
 */
package roslab.processors;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import roslab.model.general.Configuration;

/**
 * @author Peter Gebhard
 */
public abstract class ModelProcessor {

    protected Configuration config;
    protected StringTemplateGroup stg = new StringTemplateGroup("templates");
    protected StringTemplate st;

    /**
     *
     */
    public ModelProcessor(Configuration config) {
        this.config = config;
    }

    public abstract String output();

}
