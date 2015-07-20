/**
 *
 */
package roslab.model.verification;


/**
 * @author Peter Gebhard
 */
public class ElectricalSpecs extends SpecificationSection {

    SpecificationList nets = new SpecificationList();

    /**
     */
    public ElectricalSpecs() {
    }

    /**
     * @param designFile
     */
    public ElectricalSpecs(String designFile) {
        super(designFile);
    }

    /**
     * @param designFile
     * @param defaults
     */
    public ElectricalSpecs(SpecificationList defaults) {
        super(defaults);
    }

    /**
     * @param designFile
     * @param defaults
     */
    public ElectricalSpecs(String designFile, SpecificationList defaults) {
        super(designFile, defaults);
    }

    /**
     * @param designFile
     * @param defaults
     * @param nets
     */
    public ElectricalSpecs(String designFile, SpecificationList defaults, SpecificationList nets) {
        super(designFile, defaults);
        this.nets = nets;
    }

    /**
     * @return the nets
     */
    public SpecificationList getNets() {
        return nets;
    }

    /**
     * @param nets
     *            the nets to set
     */
    public void setNets(SpecificationList nets) {
        this.nets = nets;
    }

}
