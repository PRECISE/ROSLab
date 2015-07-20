/**
 *
 */
package roslab.model.verification;

/**
 * @author Peter Gebhard
 */
public class VerificationDetails {

    String name;
    VerificationType type;
    String description;

    ElectricalSpecs elecSpecs;
    MechanicalSpecs mechSpecs;

    /**
     * @param name
     * @param type
     * @param description
     * @param elecSpecs
     * @param mechSpecs
     */
    public VerificationDetails(String name, VerificationType type, String description, ElectricalSpecs elecSpecs, MechanicalSpecs mechSpecs) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.elecSpecs = elecSpecs;
        this.mechSpecs = mechSpecs;
    }

}
