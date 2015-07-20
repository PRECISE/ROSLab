/**
 *
 */
package roslab.model.verification;

/**
 * @author Peter Gebhard
 */
public class Specification {

    String name = null;
    SpecificationType type = null;
    Value value = null;
    Specification parent = null;
    SpecificationList subspecs = new SpecificationList();

    /**
     * @param name
     * @param type
     */
    public Specification(String name) {
        this.name = name;
        this.type = SpecificationType.valueOf(name.substring(0, 1).toUpperCase() + name.substring(1));
    }

    /**
     * @param name
     * @param type
     */
    public Specification(String name, SpecificationType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @param name
     * @param type
     */
    public Specification(String name, String type) {
        this.name = name;
        this.type = SpecificationType.valueOf(type.substring(0, 1).toUpperCase() + type.substring(1));
    }

    /**
     * @param name
     * @param type
     * @param value
     */
    public Specification(String name, SpecificationType type, Value value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * @param name
     * @param type
     * @param value
     * @param parent
     */
    public Specification(String name, SpecificationType type, Value value, Specification parent) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.parent = parent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public SpecificationType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(SpecificationType type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public Value getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * @return the parent
     */
    public Specification getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Specification parent) {
        this.parent = parent;
    }

    /**
     * @param spec
     *            the spec to add
     */
    public void addSubspec(Specification spec) {
        this.subspecs.addSpec(spec);
    }

    /**
     * @param spec
     *            the spec to remove
     */
    public void removeSubspec(Specification spec) {
        this.subspecs.removeSpec(spec);
    }

    /**
     * @return the subspecs
     */
    public SpecificationList getSubspecs() {
        return subspecs;
    }

    /**
     * @param subspecs
     *            the subspecs to set
     */
    public void setSubspecs(SpecificationList subspecs) {
        this.subspecs = subspecs;
    }

}
