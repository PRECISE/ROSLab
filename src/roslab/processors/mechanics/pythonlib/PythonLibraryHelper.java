/**
 *
 */
package roslab.processors.mechanics.pythonlib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.yaml.snakeyaml.Yaml;

import com.sun.xml.internal.ws.wsdl.writer.document.PortType;

/**
 * @author shaz
 */
public class PythonLibraryHelper {
    Path library = FileSystems.getDefault().getPath("mechanics_lib");

    public PythonLibraryHelper() {

    }

    public List<String> getComponentNames() {
        List<String> out = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.library, "*.py")) {
            for (Path entry : stream) {
                // Skip names that begin with underscore
                if (entry.getFileName().toString().startsWith("_")) {
                    continue;
                }
                out.add(entry.getFileName().toString().replace(".py", ""));
            }
        }
        catch (IOException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        return out;
    }

    public List<String> getYAMLComponentNames() {
        List<String> out = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.library, "*.yaml")) {
            for (Path entry : stream) {
                out.add(entry.getFileName().toString().replace(".yaml", ""));
            }
        }
        catch (IOException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        return out;
    }

    public Component loadComponentFromYaml(String componentName) throws FileNotFoundException {
        InputStream input = new FileInputStream(FileSystems.getDefault().getPath(library.toString(), componentName + ".yaml").toFile());

        Yaml yaml = new Yaml();

        Map<String, Object> yam = (Map<String, Object>) yaml.load(input);
        // System.out.println(yam);

        List<Connection> connections = new ArrayList<Connection>();
        for (List<Object> c : (List<List<Object>>) yam.get("connections")) {
            Connection conn = new Connection((List<String>) c.get(0), (List<String>) c.get(1), ConnectionType.valueOf((String) c.get(2)),
                    (Map<String, String>) c.get(3));
            connections.add(conn);
        }

        Map<String, Map<String, String>> interfaces = new HashMap<String, Map<String, String>>();
        for (Entry<String, Map<String, String>> i : ((Map<String, Map<String, String>>) yam.get("interfaces")).entrySet()) {
            interfaces.put(i.getKey(), i.getValue());
        }

        Map<String, String> parameters = (Map<String, String>) yam.get("parameters");

        Map<String, ComponentDetails> subcomponents = new HashMap<String, ComponentDetails>();
        for (Entry<String, Map<String, Object>> i : ((Map<String, Map<String, Object>>) yam.get("subcomponents")).entrySet()) {
            ComponentDetails details = new ComponentDetails((String) i.getValue().get("object"), (Map<String, List<String>>) i.getValue().get(
                    "parameters"));
            subcomponents.put(i.getKey(), details);
        }

        return new Component(connections, interfaces, parameters, subcomponents);
    }

    public Component loadComponentByPython(String componentName) {
        Properties props = new Properties();
        props.setProperty("python.path", "$PYTHONPATH:library");
        PythonInterpreter.initialize(System.getProperties(), props, new String[] { "" });

        // Create an instance of the PythonInterpreter
        PythonInterpreter interp = new PythonInterpreter();

        interp.exec("from " + componentName + " import " + componentName);

        PyObject connections = interp.eval(componentName + "().connections");
        System.out.println("connections: " + connections);
        PyObject interfaces = interp.eval(componentName + "().interfaces");
        System.out.println("interfaces: " + interfaces);
        PyObject parameters = interp.eval(componentName + "().parameters");
        System.out.println("parameters: " + parameters);
        PyObject subcomponents = interp.eval(componentName + "().subcomponents");
        System.out.println("subcomponents: " + subcomponents);

        // List<Connection> connections = new ArrayList<Connection>();
        // for(List<Object> c : (List<List<Object>>) yam.get("connections")) {
        // Connection conn = new Connection((List<String>) c.get(0),
        // (List<String>) c.get(1), ConnectionType.valueOf((String) c.get(2)),
        // (Map<String, String>) c.get(3));
        // connections.add(conn);
        // }
        // Map<String, Map<String,String>> interfaces = new HashMap<String,
        // Map<String,String>>();
        // for(Entry<String, Map<String,String>> i :
        // ((Map<String,Map<String,String>>) yam.get("interfaces")).entrySet())
        // {
        // interfaces.put(i.getKey(), i.getValue());
        // }
        // Map<String, String> parameters = (Map<String, String>)
        // yam.get("parameters");
        // Map<String, ComponentDetails> subcomponents = new HashMap<String,
        // ComponentDetails>();
        // for(Entry<String, Map<String,Object>> i :
        // ((Map<String,Map<String,Object>>)
        // yam.get("subcomponents")).entrySet()) {
        // ComponentDetails details = new ComponentDetails((String)
        // i.getValue().get("object"), (Map<String, List<String>>)
        // i.getValue().get("parameters"));
        // subcomponents.put(i.getKey(), details);
        // }
        // return new Component(connections, interfaces, parameters,
        // subcomponents);
        return null;
    }

    public ModuleSpec getDefaultModuleSpec(String compName) {
        LinkedList<PortName> sendPortNames = new LinkedList<PortName>();
        LinkedList<PortType> sendPortTypes = new LinkedList<PortType>();
        sendPortNames.add(new PortName("topright"));
        sendPortNames.add(new PortName("bottomright"));
        sendPortNames.add(new PortName("topleft"));
        sendPortNames.add(new PortName("bottomleft"));
        PortType type = new PortType(PortTypes.JOINT_TYPE_NAME);
        type.rosType = PortTypes.getJointType().rosType;
        sendPortTypes.add(type);
        sendPortTypes.add(type);
        sendPortTypes.add(type);
        sendPortTypes.add(type);
        ComponentSignature defCompSig = new ComponentSignature(compName, sendPortNames, new LinkedList<PortName>(), sendPortTypes,
                new LinkedList<PortType>(), new LinkedList<TaskDescriptor>());
        ModuleSpec def = new ModuleSpec(compName, "ROS Service", defCompSig, null);
        def.subtype = "joint";
        return def;
    }

    public ModuleSpec getModuleSpecForComponent(String compName, Component comp) {
        LinkedList<PortName> sendPortNames = new LinkedList<PortName>();
        LinkedList<PortType> sendPortTypes = new LinkedList<PortType>();
        PortType type = new PortType(PortTypes.JOINT_TYPE_NAME);
        type.rosType = PortTypes.getJointType().rosType;

        for (Entry<String, Map<String, String>> en : comp.getInterfaces().entrySet()) {
            sendPortNames.add(new PortName(en.getKey()));
            sendPortTypes.add(type);
        }
        ComponentSignature sig = new ComponentSignature(compName, sendPortNames, new LinkedList<PortName>(), sendPortTypes,
                new LinkedList<PortType>(), new LinkedList<TaskDescriptor>());
        ModuleSpec def = new ModuleSpec(compName, "ROS Service", sig, comp.clone());
        def.subtype = "joint";

        return def;
    }

    public void saveYAMLForConfiguration(ConfigurationSpec config) {
        FileWriter output = null;
        try {
            output = new FileWriter(FileSystems.getDefault().getPath(library.toString(), config.configurationName + ".yaml").toFile());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Yaml yaml = new Yaml();

        List<Connection> connections = new ArrayList<Connection>();
        for (Channel c : config.channels.values()) {
            connections.add(c.getConnection());
        }

        Map<String, Map<String, String>> interfaces = new HashMap<String, Map<String, String>>();
        for (Interface i : config.interfaces) {
            interfaces.put(i.getName(), i.getParameters());
        }

        Map<String, String> parameters = new HashMap<String, String>();
        for (int i = 0; config.params[i][0] != null; i++) {
            parameters.put(config.params[i][0], config.params[i][1]);
        }

        Map<String, Map<String, Object>> subcomponents = new HashMap<String, Map<String, Object>>();
        for (Entry<String, Pair<ModuleSpec, ComponentLocation>> e : config.configurationComponents.entrySet()) {
            Map<String, List<String>> modParams = new HashMap<String, List<String>>();
            Map<String, String> params = e.getValue().fst.pyComp.getParameters();
            for (Entry<String, String> en : params.entrySet()) {
                List<String> plist = new ArrayList<String>();
                StringTokenizer st = new StringTokenizer(en.getValue() != null ? en.getValue() : "", ",");
                while (st.hasMoreTokens()) {
                    plist.add(st.nextToken().trim());
                }
                modParams.put(en.getKey(), plist);
            }
            Map<String, Object> details = new HashMap<String, Object>();
            details.put("object", e.getValue().fst.type);
            details.put("parameters", modParams);
            subcomponents.put(e.getKey(), details);
        }

        // Dump YAML to file
        // yaml.dump(new Component(connections, interfaces, parameters,
        // subcomponents), output);
        Map<String, Object> comp = new HashMap<String, Object>();
        List<List<Object>> conns = new ArrayList<List<Object>>();
        for (Connection c : connections) {
            conns.add(c.getGenericList());
        }
        comp.put("connections", conns);
        comp.put("interfaces", interfaces);
        comp.put("parameters", parameters);
        comp.put("subcomponents", subcomponents);
        yaml.dump(comp, output);

        try {
            output.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PythonLibraryHelper p = new PythonLibraryHelper();
        System.out.println(p.getComponentNames());
        Component c = null;
        p.loadComponentByPython("Fulcrum");
        Yaml yaml = new Yaml();
        System.out.println(yaml.dump(c));
    }
}
