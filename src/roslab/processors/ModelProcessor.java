/**
 *
 */
package roslab.processors;

import java.nio.file.Paths;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import roslab.model.general.Configuration;

/**
 * @author Peter Gebhard
 */
public abstract class ModelProcessor {

    protected Configuration config;
    static protected StringTemplateGroup group = new StringTemplateGroup("ros_templates", Paths.get("resources", "software_lib", "ros_templates")
            .toString());
    protected StringTemplate st;

    /**
     *
     */
    public ModelProcessor(Configuration config) {
        this.config = config;
    }

    public abstract String output();

}
