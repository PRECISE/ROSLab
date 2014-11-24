/**
 *
 */
package roslab.model.software;

import java.util.List;

/**
 * @author Peter Gebhard
 */
public class Platform {
    public String name;
    public List<Device> devices;

    public Platform() {

    }

    public Platform(String name, List<Device> devices) {
        this.name = name;
        this.devices = devices;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Platform [" + (name != null ? "name=" + name + ", " : "") + (devices != null ? "devices=" + devices : "") + "]";
    }

    public static class Device {
        public String name;
        public String topic;
        public ROSPortType msg_type;

        public Device(String name, String topic, ROSPortType type) {
            this.name = name;
            this.topic = topic;
            this.msg_type = type;
        }

        static public ROSNode buildROSNodeFromDevice(Device dev) {
            ROSNode rn = new ROSNode(dev.name);
            rn.addPort(new ROSPort(dev.topic, rn, dev.msg_type, dev.topic, false, false, false));
            return rn;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Device [" + (name != null ? "name=" + name + ", " : "") + (topic != null ? "topic=" + topic + ", " : "")
                    + (msg_type != null ? "msg_type=" + msg_type : "") + "]";
        }
    }
}
