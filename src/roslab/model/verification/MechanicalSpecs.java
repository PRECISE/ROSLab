/**
 *
 */
package roslab.model.verification;


/**
 * @author Peter Gebhard
 */
public class MechanicalSpecs extends SpecificationSection {

    SpecificationList coreSpecs = new SpecificationList();
    SpecificationList inputs = new SpecificationList();
    SpecificationList outputs = new SpecificationList();

    /**
     */
    public MechanicalSpecs() {
    }

    /**
     * @param designFile
     */
    public MechanicalSpecs(String designFile) {
        super(designFile);
    }

    /**
     * @param designFile
     * @param defaults
     */
    public MechanicalSpecs(SpecificationList defaults) {
        super(defaults);
    }

    /**
     * @param designFile
     * @param defaults
     */
    public MechanicalSpecs(String designFile, SpecificationList defaults) {
        super(designFile, defaults);
    }

    /**
     * @param designFile
     * @param defaults
     * @param nets
     */
    public MechanicalSpecs(String designFile, SpecificationList defaults, SpecificationList inputs, SpecificationList outputs) {
        super(designFile, defaults);
        this.inputs = inputs;
        this.outputs = outputs;
    }

    /**
     * @return the coreSpecs
     */
    public SpecificationList getCoreSpecs() {
        return coreSpecs;
    }

    /**
     * @param coreSpecs
     *            the coreSpecs to set
     */
    public void setCoreSpecs(SpecificationList coreSpecs) {
        this.coreSpecs = coreSpecs;
    }

    /**
     * @return the inputs
     */
    public SpecificationList getInputs() {
        return inputs;
    }

    /**
     * @param inputs
     *            the inputs to set
     */
    public void setInputs(SpecificationList inputs) {
        this.inputs = inputs;
    }

    /**
     * @return the outputs
     */
    public SpecificationList getOutputs() {
        return outputs;
    }

    /**
     * @param outputs
     *            the outputs to set
     */
    public void setOutputs(SpecificationList outputs) {
        this.outputs = outputs;
    }

}
