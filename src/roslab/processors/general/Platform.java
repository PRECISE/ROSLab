/**
 *
 */
package roslab.processors.general;

import java.util.List;

import roslab.model.software.ROSPortType;

/**
 * @author Peter Gebhard
 */
public class Platform {
    public String name;
    public List<Device> devices;

    public class Device {
        public String name;
        public String topic;
        public ROSPortType msg_type;

        public Device(String name, String topic, ROSPortType type) {
            this.name = name;
            this.topic = topic;
            this.msg_type = type;
        }
    }
}
