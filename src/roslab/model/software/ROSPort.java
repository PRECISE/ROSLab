/**
 *
 */
package roslab.model.software;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Link;
import roslab.model.ui.UIEndpoint;

/**
 * @author Peter Gebhard
 */
public class ROSPort extends Feature implements Endpoint {

    ROSTopic topic;

    // TODO: Make these fields part of the annotations list instead?
    boolean fanIn;
    boolean fanOut;

    List<Link> links = new ArrayList<Link>();

    /**
     * @param name
     * @param parent
     * @param annotations
     * @param topic
     * @param fanIn
     * @param fanOut
     */
    public ROSPort(String name, ROSNode parent, Map<String, String> annotations, ROSTopic topic, boolean fanIn, boolean fanOut) {
        super(name, parent, annotations);
        if (topic == null) {
            throw new IllegalArgumentException("Bad topic.");
        }
        else {
            this.annotations.put("Topic", topic.getTopicName());
            this.topic = topic;
        }
        this.fanIn = fanIn && topic.isSubscriber();
        this.fanOut = fanOut && !topic.isSubscriber();
    }

    /**
     * @param name
     * @param parent
     * @param type
     * @param topic
     * @param direction
     * @param fanIn
     * @param fanOut
     */
    public ROSPort(String name, ROSNode parent, ROSTopic topic, boolean fanIn, boolean fanOut) {
        super(name, parent);
        if (topic == null) {
            throw new IllegalArgumentException("Bad topic.");
        }
        else {
            this.annotations.put("Topic", topic.getTopicName());
            this.topic = topic;
        }
        this.fanIn = fanIn && topic.isSubscriber();
        this.fanOut = fanOut && !topic.isSubscriber();
    }

    /**
     * @return the type
     */
    public ROSMsgType getType() {
        return topic.getType();
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(ROSMsgType type) {
        topic.setType(type);
    }

    /**
     * @return the direction
     */
    public boolean isSubscriber() {
        return topic.isSubscriber();
    }

    /**
     * @param direction
     *            the direction to set
     */
    public void setDirection(boolean direction) {
        topic.setDirection(direction);
    }

    /**
     * @return the topic
     */
    public String getTopicName() {
        return this.annotations.get("Topic");
    }

    /**
     * @param topic
     *            the topic to set
     */
    public void setTopicName(String topicName) {
        annotations.put("Topic", topicName);
        this.topic.setTopicName(topicName);
    }

    /**
     * @return the topic
     */
    public ROSTopic getTopic() {
        return topic;
    }

    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(ROSTopic topic) {
        this.topic = topic;
    }

    /**
     * @param fanIn
     *            the fanIn to set
     */
    public void setFanIn(boolean fanIn) {
        this.fanIn = fanIn;
    }

    /**
     * @param fanOut
     *            the fanOut to set
     */
    public void setFanOut(boolean fanOut) {
        this.fanOut = fanOut;
    }

    /**
     * @return the links
     */
    @Override
    public List<Link> getLinks() {
        return links;
    }

    /**
     * @param links
     *            the links to set
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanIn()
     */
    @Override
    public boolean isFanIn() {
        return fanIn && topic.isSubscriber();
    }

    /*
     * (non-Javadoc)
     * @see roslab.model.general.Endpoint#isFanOut()
     */
    @Override
    public boolean isFanOut() {
        return fanOut && !topic.isSubscriber();
    }

    /*
     * (non-Javadoc)
     * @see
     * roslab.model.general.Endpoint#canConnect(roslab.model.general.Endpoint)
     */
    @Override
    public boolean canConnect(Endpoint e) {
        // Only allow ROSPorts to connect to other ROSPorts
        if (e instanceof ROSPort) {
            ROSPort p = (ROSPort) e;

            // Valid connection if directions are opposite, but topics match.
            return (this.isSubscriber() != p.isSubscriber()) && (topic.getTopicName().equals(p.getTopic().getTopicName()));
        }
        return false;
    }

    @Override
    public Link connect(Endpoint e) {
        if (e instanceof ROSPort) {
            ROSPort src;
            ROSPort dest;

            if (((ROSPort) e).isSubscriber()) {
                dest = (ROSPort) e;
                src = this;
            }
            else {
                src = (ROSPort) e;
                dest = this;
            }

            Link l = new Link(src, dest);
            src.links.add(l);
            dest.links.add(l);

            return l;
        }
        return null;
    }

    @Override
    public void disconnect(Link l) {
        links.remove(l);
    }

    @Override
    public UIEndpoint getUIEndpoint() {
        return parent.getUINode().getUIEndpoint(this);
    }

    @Override
    public boolean isInput() {
        return topic.isSubscriber();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name + " (" + topic.toString() + ")";
    }

    public ROSPort getClone(String name, ROSNode parent) {
        return new ROSPort(name, parent, this.getAnnotationsCopy(), topic.clone(), fanIn, fanIn);
    }

}
