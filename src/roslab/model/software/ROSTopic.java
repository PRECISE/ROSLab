/**
 *
 */
package roslab.model.software;

/**
 * @author Peter Gebhard
 */
public class ROSTopic {
    private String topic;
    private ROSMsgType type;
    private boolean direction; // true is in/subscribe, false is out/publish

    /**
     * @param topic
     * @param type
     * @param direction
     */
    public ROSTopic(String topic, ROSMsgType type, boolean direction) {
        if (topic == null) {
            throw new IllegalArgumentException("Bad topic.");
        }
        else {
            this.topic = topic;
        }
        if (type == null) {
            throw new IllegalArgumentException("Bad type.");
        }
        else {
            this.type = type;
        }
        this.direction = direction;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * @return the type
     */
    public ROSMsgType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(ROSMsgType type) {
        this.type = type;
    }

    /**
     * @return the direction
     */
    public boolean isSubscriber() {
        return direction;
    }

    /**
     * @param direction
     *            the direction to set
     */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ROSTopic [" + (topic != null ? "topic=" + topic + ", " : "") + (type != null ? "type=" + type + ", " : "") + "direction=" + direction
                + "]";
    }
}
