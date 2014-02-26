package roslab.model.transforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import roslab.artifacts.ComponentLocation;
import roslab.artifacts.ConfigurationSpec;
import roslab.artifacts.ModuleSpec;
import roslab.artifacts.WorkspaceContext;
import roslab.types.builtins.Channel;
import roslab.types.builtins.CompRoles;
import roslab.types.builtins.ComponentSignature;
import roslab.types.builtins.PortName;
import roslab.types.builtins.PortType;
import roslab.utils.Pair;

/**
 * Takes a given "app" configuration and then generates
 * ROS nodes designed to work together
 * @author aking
 *
 */
public class ROSModelCompiler {
	
	protected StringTemplateGroup group = new StringTemplateGroup("templates");
	
	public Map<String, String> generateNodeFiles(String appName){
		int topicSeed = 0;
		Map<String, String> appBundle = new HashMap<String, String>();
		if(WorkspaceContext.currentWorkspace.configurations.containsKey(appName)){
			ConfigurationSpec appSpec = WorkspaceContext.currentWorkspace.configurations.get(appName);
			HashMap<Pair<String, String>, String> portTopicMap = new HashMap<Pair<String, String>, String>();
			//now we need to walk all the connections, check their role, and see if we need a new topic or not
			for(Channel chan : appSpec.channels.values()){
				ModuleSpec pubCompSpec = chan.getPubComp().snd;
				ModuleSpec subCompSpec = chan.getSubComp().snd;
				if(pubCompSpec.role.equals(CompRoles.ROS_SERVICE)) {
					//the publisher is a service, thus no topic needs to be made
					portTopicMap.put(new Pair<String, String>(chan.getSubComp().fst, chan.getSubName()), chan.getPubName());
				}
				else if(subCompSpec.role.equals(CompRoles.ROS_SERVICE)) {
					//the subscriber is a service, thus no topic needs to be generated
					portTopicMap.put(new Pair<String, String>(chan.getPubComp().fst, chan.getPubName()), chan.getSubName());
				}
				else {
					String freshTopic = appName + topicSeed++;
					portTopicMap.put(new Pair<String, String>(chan.getPubComp().fst, chan.getPubName()), freshTopic);
					portTopicMap.put(new Pair<String, String>(chan.getSubComp().fst, chan.getSubName()), freshTopic);
				}
			}
			for(String instName : appSpec.configurationComponents.keySet()) {
				ModuleSpec mspec = appSpec.configurationComponents.get(instName).fst;

                if(mspec.type.equals("GUI")) {
                    String genCode = buildGui(instName, portTopicMap, mspec, appSpec.configurationComponents, group);
                    appBundle.put(instName, genCode);
                }
				else if(mspec.role.equals(CompRoles.ROS_NODE)) {
					String genCode = buildNode(instName, portTopicMap, mspec, group);
					appBundle.put(instName, genCode);
				}
			}
		}
		return appBundle;
	}
	
	public String buildNode(String instName, Map<Pair<String, String>, String> portTopicMap, ModuleSpec mspec, StringTemplateGroup group){
		StringTemplate rosNodeTemplate = group.getInstanceOf("ROSNode");
		String includes = "#include <stdlib.h>\n";
		includes += "#include <signal.h>\n";
		includes += "#include <ros/ros.h>\n";
		
		ComponentSignature csig = mspec.sig;
		Set<String> includeDeps = collectIncludes(csig);
		for(String dep : includeDeps) {
            StringTemplate includeTemplate = group.getInstanceOf("ROSInclude");
			includeTemplate.setAttribute("incl_file", dep);
			includes += includeTemplate.toString() + "\n";
		}
		
		//add the includes
		rosNodeTemplate.setAttribute("includes", includes);
		//now get all the pub ports
		String pubPortDecls = "";
		for(PortName pubPortName : csig.getSendPortNames()) {
			String portName = pubPortName.getName();
			pubPortDecls += "ros::Publisher " + portName + ";\n";
		}
		rosNodeTemplate.setAttribute("pub_ports", pubPortDecls);
		//now declare variables representing the latest value from each port
		String portValueStr = "";
		String subCallbacks = "";
		String cbSetupStr = "";
		for(int i = 0; i < csig.getRecvPortNames().size(); i++) {
			//first collect all the values
			PortName portName = csig.getRecvPortNames().get(i);
			PortType portType = csig.getRecvPortTypes().get(i);
			
			//then generate callbacks
			StringTemplate callbackTemp = group.getInstanceOf("ROSCallback");
			callbackTemp.setAttribute("port_name",  portName.getName());
			callbackTemp.setAttribute("port_type_name", portType.rosType);
			subCallbacks += callbackTemp.toString() + "\n";
			//generate the callback setups
            StringTemplate cbSetupTemp = group.getInstanceOf("ROSSubscriber");
			cbSetupTemp.setAttribute("port_name", portName.getName());
			cbSetupTemp.setAttribute("port_type", portType.rosType);
			cbSetupTemp.setAttribute("port_topic", portTopicMap.get(new Pair<String, String>(instName, portName.getName())));
			cbSetupStr += cbSetupTemp.toString() + "\n";
		}
		String pubPortSetupStr = "";
		for(int i = 0; i < csig.getSendPortNames().size(); i++) {
			PortName portName = csig.getSendPortNames().get(i);
			PortType portType = csig.getSendPortTypes().get(i);
            StringTemplate pubSetupTemp = group.getInstanceOf("ROSPublisher");
			pubSetupTemp.setAttribute("port_type", portType.rosType);
			pubSetupTemp.setAttribute("port_name", portName.getName());
			pubSetupTemp.setAttribute("port_topic", portTopicMap.get(new Pair<String, String>(instName, portName.getName())));
			pubPortSetupStr += pubSetupTemp.toString() + "\n";
		}
		rosNodeTemplate.setAttribute("sub_port_values", portValueStr);
		rosNodeTemplate.setAttribute("sub_port_callbacks", subCallbacks);
		rosNodeTemplate.setAttribute("sub_callback_setup", cbSetupStr);
		rosNodeTemplate.setAttribute("pub_connections", pubPortSetupStr);
		return rosNodeTemplate.toString();
	}
	
	public String buildGui(String instName, Map<Pair<String, String>, String> portTopicMap, ModuleSpec mspec, Map<String, Pair<ModuleSpec, ComponentLocation>> configurationComponents, StringTemplateGroup group){
        StringTemplate rosGuiTemplate = group.getInstanceOf("ROSGui");
		
		//TODO: Update to add ability for choosing button name from existing Node component name (and multiple control buttons)
		for(String name : configurationComponents.keySet()) {
			ModuleSpec m = configurationComponents.get(name).fst;

            if(m.role.equals(CompRoles.ROS_NODE)) {
            	rosGuiTemplate.setAttribute("control_button_name", "\"" + name + "\"");
			}
		}
		
		ComponentSignature csig = mspec.sig;

        String subDataDefsStr = "";
		String subInputDataPlotsStr = "";
		String subOutputDataPlotsStr = "";
		String subDataPlotUpdatesStr = "";
        String subsStr = "";
		String subCallbacksStr = "";
		
		for(int i = 0; i < csig.getRecvPortNames().size(); i++) {
			PortName portName = csig.getRecvPortNames().get(i);
			PortType portType = csig.getRecvPortTypes().get(i);

            // generate subscriber data definitions
            StringTemplate subDataDefsTemplate = group.getInstanceOf("ROSGuiSubscriberDataDefs");
            subDataDefsTemplate.setAttribute("port_name", portName.getName());
            subDataDefsStr += subDataDefsTemplate.toString() + "\n";

            // generate subscriber input data plots
            StringTemplate subInputDataPlotsTemplate = group.getInstanceOf("ROSGuiSubscriberInputDataPlots");
            subInputDataPlotsTemplate.setAttribute("port_name", portName.getName());
            subInputDataPlotsStr += subInputDataPlotsTemplate.toString() + "\n";

            // generate subscriber output data plots
            StringTemplate subOutputDataPlotsTemplate = group.getInstanceOf("ROSGuiSubscriberOutputDataPlots");
            subOutputDataPlotsTemplate.setAttribute("port_name", portName.getName());
            subOutputDataPlotsStr += subOutputDataPlotsTemplate.toString() + "\n";

            // generate subscriber data plot updates
            StringTemplate subDataPlotUpdatesTemplate = group.getInstanceOf("ROSGuiSubscriberDataPlotUpdates");
            subDataPlotUpdatesTemplate.setAttribute("port_name", portName.getName());
            subDataPlotUpdatesStr += subDataPlotUpdatesTemplate.toString() + "\n";

            // generate subscribers
            StringTemplate subsTemplate = group.getInstanceOf("ROSGuiSubscriber");
            subsTemplate.setAttribute("port_name", portName.getName());
            subsTemplate.setAttribute("port_type", portType.rosType);
            subsTemplate.setAttribute("port_topic", portTopicMap.get(new Pair<String, String>(instName, portName.getName())));
            subsStr += subsTemplate.toString() + "\n";

            // generate subscriber callbacks
            StringTemplate subCallbacksTemplate = group.getInstanceOf("ROSGuiSubscriberCallback");
            subCallbacksTemplate.setAttribute("port_name", portName.getName());
            subCallbacksTemplate.setAttribute("subscriber_field", "msg.linear.x");
            subCallbacksStr += subCallbacksTemplate.toString() + "\n\n";
		}
		
		rosGuiTemplate.setAttribute("subscriber_data_defs", subDataDefsStr);
		rosGuiTemplate.setAttribute("subscriber_input_data_plots", subInputDataPlotsStr);
		rosGuiTemplate.setAttribute("subscriber_output_data_plots", subOutputDataPlotsStr);
		rosGuiTemplate.setAttribute("subscriber_data_plot_updates", subDataPlotUpdatesStr);
		rosGuiTemplate.setAttribute("subscribers", subsStr);
		//No publishers for now...  rosGuiTemplate.setAttribute("publishers", subDataPlotUpdatesStr);
		rosGuiTemplate.setAttribute("subscriber_callbacks", subCallbacksStr);
		
		return rosGuiTemplate.toString();
	}
	
	private  Set<String> collectIncludes(ComponentSignature csig) {
		Set<String> ret = new HashSet<String>();
		for(PortType type : csig.getRecvPortTypes()) {
			ret.addAll(type.includeDeps);
		}
		for(PortType type : csig.getSendPortTypes()) {
			ret.addAll(type.includeDeps);
		}
		return ret;
	}

}
