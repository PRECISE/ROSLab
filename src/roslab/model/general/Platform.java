/**
 *
 */
package roslab.model.general;

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
}
