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
 *
 */
public abstract class ModelProcessor {
	
	Configuration config;
	STGroup stg = new STRawGroupDir("templates");
	ST st;

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
