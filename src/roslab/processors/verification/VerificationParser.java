/**
 * This class is used to parse Verification YAML files.
 */
package roslab.processors.verification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import roslab.model.verification.ElectricalSpecs;
import roslab.model.verification.MechanicalSpecs;
import roslab.model.verification.Specification;
import roslab.model.verification.SpecificationList;
import roslab.model.verification.SpecificationType;
import roslab.model.verification.Value;
import roslab.model.verification.VerificationDetails;
import roslab.model.verification.VerificationType;

/**
 * @author Peter Gebhard
 */
public class VerificationParser {
    private static Yaml yaml;

    @SuppressWarnings("unchecked")
    public static VerificationDetails parseVerificationYAML(File verFile) {
        yaml = new Yaml();

        Map<String, Object> yam = new HashMap<String, Object>();

        try {
            yam = (Map<String, Object>) yaml.load(Files.newBufferedReader(verFile.toPath()));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Parse Electrical Specs
        ElectricalSpecs elecSpecs = new ElectricalSpecs();
        Map<String, Object> elec = (Map<String, Object>) yam.get("electrical");
        elecSpecs.setDesignFile((String) elec.get("design_file"));

        // Parse defaults
        Map<String, Object> elecDef = (Map<String, Object>) elec.get("defaults");
        if (elecDef != null) {
            for (Entry<String, Object> e : elecDef.entrySet()) {
                elecSpecs.getDefaults().addSpec(getSpec(e));
            }
        }

        // Parse nets
        List<Map<String, Object>> nets = (List<Map<String, Object>>) elec.get("nets");
        if (nets != null) {
            for (Map<String, Object> net : nets) {
                if (net.containsKey("name")) {
                    Specification spec = new Specification((String) net.get("name"), SpecificationType.Net);
                    spec.setSubspecs(getSpecList(net));
                    elecSpecs.getNets().addSpec(spec);
                }
            }
        }

        MechanicalSpecs mechSpecs = new MechanicalSpecs();
        Map<String, Object> mech = (Map<String, Object>) yam.get("mechanical");
        mechSpecs.setDesignFile((String) mech.get("design_file"));

        // Parse defaults
        Map<String, Object> mechDef = (Map<String, Object>) mech.get("defaults");
        if (mechDef != null) {
            for (Entry<String, Object> e : mechDef.entrySet()) {
                mechSpecs.getDefaults().addSpec(getSpec(e));
            }
        }

        // Parse core specs
        mechSpecs.setCoreSpecs(getSpecList(mech));

        // Parse Inputs
        List<Map<String, Object>> inputs = (List<Map<String, Object>>) mech.get("inputs");
        if (inputs != null) {
            for (Map<String, Object> input : inputs) {
                if (input.containsKey("name")) {
                    Specification spec = new Specification((String) input.get("name"), SpecificationType.Input);
                    spec.setSubspecs(getSpecList(input));
                    mechSpecs.getInputs().addSpec(spec);
                }
            }
        }

        // Parse Outputs
        List<Map<String, Object>> outputs = (List<Map<String, Object>>) mech.get("outputs");
        if (outputs != null) {
            for (Map<String, Object> output : outputs) {
                if (output.containsKey("name")) {
                    Specification spec = new Specification((String) output.get("name"), SpecificationType.Output);
                    spec.setSubspecs(getSpecList(output));
                    mechSpecs.getOutputs().addSpec(spec);
                }
            }
        }

        return new VerificationDetails((String) yam.get("name"), VerificationType.valueOf(yam.get("type").toString().substring(0, 1).toUpperCase()
                + yam.get("type").toString().substring(1)), (String) yam.get("description"), elecSpecs, mechSpecs);
    }

    private static Specification getSpec(Entry<String, Object> e) {
        Specification result = new Specification(e.getKey());

        // Let's get access to the contents of the entry
        @SuppressWarnings("unchecked")
        Map<String, Object> v = (Map<String, Object>) e.getValue();

        // We're at the bottom level (no more subspecs) if there is a key
        // containing 'value'
        if (v.containsKey("value")) {
            result.setValue(new Value(v.get("value").toString(), v.get("unit").toString()));
            return result;
        }

        // If we're not at the bottom level, let's get the subspecs
        for (Entry<String, Object> sub : v.entrySet()) {
            Specification subspec = getSpec(sub);
            subspec.setParent(result);
            result.addSubspec(subspec);
        }

        return result;
    }

    private static SpecificationList getSpecList(Map<String, Object> sl) {
        SpecificationList result = new SpecificationList();

        for (Entry<String, Object> s : sl.entrySet()) {
            try {
                if (SpecificationType.valueOf(s.getKey().substring(0, 1).toUpperCase() + s.getKey().substring(1)) != null) {
                    result.addSpec(getSpec(s));
                }
            }
            catch (IllegalArgumentException iae) {
                // If we tried to parse a SpecificationType that doesn't exist,
                // we'll skip it and continue looking for legit Specifications
                continue;
            }
        }

        return result;
    }
}
