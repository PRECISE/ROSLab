package roslab.processors.software;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import roslab.model.software.ROSMsgType;
import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;

/**
 * Takes a given configuration and generates the appropriate ROS node source
 * code.
 *
 * @author Peter Gebhard
 */
public class ROSNodeCodeGenerator {

    static protected StringTemplateGroup group = new StringTemplateGroup("ros_templates", Paths.get("resources", "software_lib", "ros_templates")
            .toString());  // Using Paths.get() to be able to handle OS-specific

    // filesystem differences.

    static public void buildNode(ROSNode node, File out) throws IOException {
        // Define templates
        StringTemplate nodeTemplate = group.getInstanceOf("ROSNode");

        // Generate Includes
        String includes = "#include <stdlib.h>\n#include <signal.h>\n#include <ros/ros.h>\n";
        List<String> includesList = new ArrayList<String>();
        for (ROSPort port : node.getPorts().values()) {
        	if (port.getLinks().size() == 0) {
        		System.out.println("Warning: " + node.getName() +  "_node. No link on port \"" 
        				+ port.getName() + "\". Code for this port not generated.");
        		continue;
        	}
            StringTemplate includeTemplate = group.getInstanceOf("ROSInclude");
            includeTemplate.setAttribute("include_file", ROSMsgType.typeMap.get(port.getType()) + "/" + port.getType().toString());
            if (!includes.contains(includeTemplate.toString())) {
                includesList.add(includeTemplate.toString());
            }
        }
        // Sort list of Includes
        Collections.sort(includesList);
        // Generate string output of Includes
        for (String i : includesList) {
            includes += i + "\n";
        }
        nodeTemplate.setAttribute("includes", includes);
        nodeTemplate.setAttribute("node_name", node.getName() + "_node");

        // Generate publishers (and publish commands)
        String publishers = "";
        String publishCommands = "";
        for (ROSPort port : node.getPublisherPorts().values()) {
        	if (port.getLinks().size() == 0) continue;
            StringTemplate publisherTemplate = group.getInstanceOf("ROSPublisher");
            StringTemplate publishCommandTemplate = group.getInstanceOf("ROSPublishCommand");
            String pName = port.getName().replace("/","");
            publisherTemplate.setAttribute("port_name", pName);
            publisherTemplate.setAttribute("port_type", ROSMsgType.typeMap.get(port.getType()) + "::" + port.getType().toString());
            publisherTemplate.setAttribute("port_topic", port.getTopicName());
            publishers += publisherTemplate.toString() + "\n";

            publishCommandTemplate.setAttribute("port_name", pName);
            publishCommands += publishCommandTemplate.toString() + "\n";
        }
        nodeTemplate.setAttribute("publishers", publishers);
        nodeTemplate.setAttribute("publish_commands", publishCommands);

        // Generate subscribers (and callbacks)
        String subscribers = "";
        String subscriberCallbacks = "";
        for (ROSPort port : node.getSubscriberPorts().values()) {
        	if (port.getLinks().size() == 0) continue;
            StringTemplate subscriberTemplate = group.getInstanceOf("ROSSubscriber");
            StringTemplate subscriberCallbackTemplate = group.getInstanceOf("ROSCallback");
            String pName = port.getName().replace("/","");
            subscriberTemplate.setAttribute("port_name", pName);
            subscriberTemplate.setAttribute("port_type", ROSMsgType.typeMap.get(port.getType()) + "::" + port.getType().toString());
            subscriberTemplate.setAttribute("port_topic", port.getTopicName());
            subscribers += subscriberTemplate.toString() + "\n";

            subscriberCallbackTemplate.setAttribute("port_name", pName);
            subscriberCallbackTemplate.setAttribute("port_type", ROSMsgType.typeMap.get(port.getType()) + "::" + port.getType().toString());
            subscriberCallbacks += subscriberCallbackTemplate.toString() + "\n";
        }
        nodeTemplate.setAttribute("subscribers", subscribers);
        nodeTemplate.setAttribute("subscriber_callbacks", subscriberCallbacks);

        nodeTemplate.setAttribute("rate", node.getRate());

        BufferedWriter writer = Files.newBufferedWriter(out.toPath(), Charset.defaultCharset());
        writer.write(nodeTemplate.toString());
        writer.close();
    }

}
