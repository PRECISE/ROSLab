/**
 *
 */
package roslab.model.software;

/**
 * @author Peter Gebhard
 */
public class ROSTopic implements Cloneable {
    private String topicName;
    private ROSMsgType type;
    private boolean direction; // true is in/subscribe, false is out/publish

    /**
     * @param topicName
     * @param type
     * @param direction
     */
    public ROSTopic(String topicName, ROSMsgType type, boolean direction) {
        if (topicName == null) {
            throw new IllegalArgumentException("Bad topicName.");
        }
        else {
            this.topicName = topicName;
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
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @param topicName
     *            the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
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

    @Override
    public ROSTopic clone() {
        return new ROSTopic(topicName, type, direction);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (topicName != null ? "topicName=" + topicName + ", " : "") + (type != null ? "type=" + type + ", " : "") + "direction="
                + (direction ? "subscribe" : "publish");
    }
}
