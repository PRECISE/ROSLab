/**
 *
 */
package roslab.processors.software;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import roslab.model.software.Platform;
import roslab.model.software.Platform.Device;
import roslab.model.software.ROSMsgType;

/**
 * @author Peter Gebhard
 */
public class PlatformParser {
    private Yaml yaml = new Yaml();
    private Map<String, Object> yam;
    public Platform platform = new Platform();

    @SuppressWarnings("unchecked")
    public PlatformParser(File platformFile) {
        try {
            yam = (Map<String, Object>) yaml.load(Files.newBufferedReader(platformFile.toPath()));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        List<Platform.Device> devices = new ArrayList<Platform.Device>();

        for (Map<String, Object> d : (List<Map<String, Object>>) yam.get("devices")) {
            devices.add(new Device((String) d.get("name"), (String) d.get("topic"), new ROSMsgType(
                    (String) ((Map<String, Object>) d.get("msg_type")).get("type"))));
        }

        platform = new Platform((String) yam.get("name"), devices);
    }
}
