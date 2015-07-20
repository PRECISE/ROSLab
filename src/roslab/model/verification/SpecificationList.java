/**
 *
 */
package roslab.model.verification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Gebhard
 */
public class SpecificationList {

    List<Specification> specs = new ArrayList<Specification>();

    /**
     * @param name
     *            the name of the specification to get
     * @return the specification
     */
    public Specification getSpec(String name) {
        for (Specification s : specs) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @param spec
     *            the specification to be added
     */
    public void addSpec(Specification spec) {
        if (spec == null) {
            throw new IllegalArgumentException();
        }
        if (specs.indexOf(getSpec(spec.getName())) != -1) {
            specs.set(specs.indexOf(getSpec(spec.getName())), spec);
        }
        else {
            specs.add(spec);
        }
    }

    /**
     * @param name
     *            the name of the specification to be removed
     */
    public boolean removeSpec(String name) {
        if (getSpec(name) == null) {
            throw new IllegalArgumentException();
        }
        return specs.remove(getSpec(name));
    }

    /**
     * @param spec
     *            the specification to be removed
     */
    public boolean removeSpec(Specification spec) {
        if (spec == null) {
            throw new IllegalArgumentException();
        }
        return specs.remove(spec);
    }

}
