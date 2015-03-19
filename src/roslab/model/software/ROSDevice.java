/**
 *
 */
package roslab.model.software;

import roslab.model.general.Device;

/**
 * @author Peter Gebhard
 */
public class ROSDevice extends Device {

    public String topic;
    public String direction;
    public ROSMsgType msg_type;

    /**
     * @param name
     * @param dev_type
     * @param topic
     * @param direction
     * @param type
     */
    public ROSDevice(String name, String topic, String direction, ROSMsgType type) {
        super(name, "ROS");
        this.topic = topic;
        this.direction = direction;
        this.msg_type = type;
    }

    public static ROSNode buildNodeFromDevice(ROSDevice dev) {
        ROSNode rn = new ROSNode(dev.name);
        rn.addPort(new ROSPort(dev.topic, rn, new ROSTopic(dev.topic, dev.msg_type, dev.direction == "sub" ? true : false), false, false));
        return rn;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ROSDevice [" + (name != null ? "name=" + name + ", " : "") + (topic != null ? "topic=" + topic + ", " : "")
                + (direction != null ? "direction=" + direction + ", " : "") + (msg_type != null ? "msg_type=" + msg_type : "") + "]";
    }

}
