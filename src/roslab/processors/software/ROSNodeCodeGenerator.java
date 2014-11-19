package roslab.processors.software;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import roslab.model.software.ROSNode;
import roslab.model.software.ROSPort;
import roslab.model.software.ROSPortType;

/**
 * Takes a given configuration and generates the appropriate ROS node source
 * code.
 *
 * @author Peter Gebhard
 */
public class ROSNodeCodeGenerator {

    static protected StringTemplateGroup group = new StringTemplateGroup("templates", "templates");

    // public Map<String, String> generateNodeFiles(String appName) {
    // int topicSeed = 0;
    // Map<String, String> appBundle = new HashMap<String, String>();
    // if
    // (WorkspaceContext.currentWorkspace.configurations.containsKey(appName)) {
    // ConfigurationSpec appSpec =
    // WorkspaceContext.currentWorkspace.configurations.get(appName);
    // HashMap<Pair<String, String>, String> portTopicMap = new
    // HashMap<Pair<String, String>, String>();
    // // now we need to walk all the connections, check their role, and
    // // see if we need a new topic or not
    // for (Channel chan : appSpec.channels.values()) {
    // ModuleSpec pubCompSpec = chan.getPubComp().snd;
    // ModuleSpec subCompSpec = chan.getSubComp().snd;
    // if (pubCompSpec.role.equals(CompRoles.ROS_SERVICE)) {
    // // the publisher is a service, thus no topic needs to be
    // // made
    // portTopicMap.put(new Pair<String, String>(chan.getSubComp().fst,
    // chan.getSubName()), chan.getPubName());
    // }
    // else if (subCompSpec.role.equals(CompRoles.ROS_SERVICE)) {
    // // the subscriber is a service, thus no topic needs to be
    // // generated
    // portTopicMap.put(new Pair<String, String>(chan.getPubComp().fst,
    // chan.getPubName()), chan.getSubName());
    // }
    // else {
    // String freshTopic = appName + topicSeed++;
    // portTopicMap.put(new Pair<String, String>(chan.getPubComp().fst,
    // chan.getPubName()), freshTopic);
    // portTopicMap.put(new Pair<String, String>(chan.getSubComp().fst,
    // chan.getSubName()), freshTopic);
    // }
    // }
    // for (String instName : appSpec.configurationComponents.keySet()) {
    // ModuleSpec mspec = appSpec.configurationComponents.get(instName).fst;
    //
    // if (mspec.type.equals("GUI")) {
    // String genCode = buildGui(instName, portTopicMap, mspec,
    // appSpec.configurationComponents, group);
    // appBundle.put(instName, genCode);
    // }
    // else if (mspec.role.equals(CompRoles.ROS_NODE)) {
    // String genCode = buildNode(instName, portTopicMap, mspec, group);
    // appBundle.put(instName, genCode);
    // }
    // }
    // }
    // return appBundle;
    // }

    static public void buildNode(ROSNode node, File out) throws IOException {
        // Define templates
        StringTemplate nodeTemplate = group.getInstanceOf("ROSNode");

        // Generate Includes
        String includes = "#include <stdlib.h>\n#include <signal.h>\n#include <ros/ros.h>\n";
        List<String> includesList = new ArrayList<String>();
        for (ROSPort port : node.getPorts().values()) {
            StringTemplate includeTemplate = group.getInstanceOf("ROSInclude");
            includeTemplate.setAttribute("include_file", ROSPortType.typeMap.get(port.getType()) + "/" + port.getType().toString());
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

        // Generate publishers (and publish commands)
        String publishers = "";
        String publishCommands = "";
        for (ROSPort port : node.getPublisherPorts().values()) {
            StringTemplate publisherTemplate = group.getInstanceOf("ROSPublisher");
            StringTemplate publishCommandTemplate = group.getInstanceOf("ROSPublishCommand");
            publisherTemplate.setAttribute("port_name", port.getName());
            publisherTemplate.setAttribute("port_type", ROSPortType.typeMap.get(port.getType()) + "::" + port.getType().toString());
            publisherTemplate.setAttribute("port_topic", port.getTopic());
            publishers += publisherTemplate.toString() + "\n";

            publishCommandTemplate.setAttribute("port_name", port.getName());
            publishCommands += publishCommandTemplate.toString() + "\n";
        }
        nodeTemplate.setAttribute("publishers", publishers);
        nodeTemplate.setAttribute("publish_commands", publishCommands);

        // Generate subscribers (and callbacks)
        String subscribers = "";
        String subscriberCallbacks = "";
        for (ROSPort port : node.getSubscriberPorts().values()) {
            StringTemplate subscriberTemplate = group.getInstanceOf("ROSSubscriber");
            StringTemplate subscriberCallbackTemplate = group.getInstanceOf("ROSCallback");
            subscriberTemplate.setAttribute("port_name", port.getName());
            subscriberTemplate.setAttribute("port_type", ROSPortType.typeMap.get(port.getType()) + "::" + port.getType().toString());
            subscriberTemplate.setAttribute("port_topic", port.getTopic());
            subscribers += subscriberTemplate.toString() + "\n";

            subscriberCallbackTemplate.setAttribute("port_name", port.getName());
            subscriberCallbackTemplate.setAttribute("port_type", ROSPortType.typeMap.get(port.getType()) + "::" + port.getType().toString());
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
