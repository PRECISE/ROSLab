/**
 *
 */
package roslab.model.verification;

/**
 * @author Peter Gebhard
 */
public abstract class SpecificationSection {

    String designFile = null;
    SpecificationList defaults = new SpecificationList();

    /**
     */
    public SpecificationSection() {
    }

    /**
     * @param designFile
     */
    public SpecificationSection(String designFile) {
        this.designFile = designFile;
    }

    /**
     * @param defaults
     */
    public SpecificationSection(SpecificationList defaults) {
        this.defaults = defaults;
    }

    /**
     * @param designFile
     * @param defaults
     */
    public SpecificationSection(String designFile, SpecificationList defaults) {
        this.designFile = designFile;
        this.defaults = defaults;
    }

    /**
     * @return the designFile
     */
    public String getDesignFile() {
        return designFile;
    }

    /**
     * @param designFile
     *            the designFile to set
     */
    public void setDesignFile(String designFile) {
        this.designFile = designFile;
    }

    /**
     * @return the defaults
     */
    public SpecificationList getDefaults() {
        return defaults;
    }

    /**
     * @param defaults
     *            the defaults to set
     */
    public void setDefaults(SpecificationList defaults) {
        this.defaults = defaults;
    }

}
