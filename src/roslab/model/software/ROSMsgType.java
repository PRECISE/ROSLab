/**
 *
 */
package roslab.model.software;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Peter Gebhard
 */
public class ROSMsgType implements Comparable<ROSMsgType> {
    public static final Map<ROSMsgType, String> typeMap;
    static {
        Map<ROSMsgType, String> aMap = new HashMap<ROSMsgType, String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("resources", "software_lib", "ros_msgs"))) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && Files.exists(entry.resolve("msg"))) {
                    for (Path msg : Files.newDirectoryStream(entry.resolve("msg"))) {
                        String type = msg.getFileName().toString().substring(0, msg.getFileName().toString().lastIndexOf('.'));
                        aMap.put(new ROSMsgType(type), entry.getFileName().toString());
                    }
                }
            }
        }
        catch (IOException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        typeMap = Collections.unmodifiableMap(aMap);
    }

    public String type;

    public ROSMsgType(String type) {
        // TODO Add validation check against existing typeMap to ensure only
        // valid ROSMsgType objects are created (ie. the type exists in the type
        // map)
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return type;
    }

    @Override
    public int compareTo(ROSMsgType other) {
        return type.compareTo(other.type);
    }

}