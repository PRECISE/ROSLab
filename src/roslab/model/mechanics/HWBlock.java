/**
 *
 */
package roslab.model.mechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import roslab.model.general.Endpoint;
import roslab.model.general.Feature;
import roslab.model.general.Node;

/**
 * @author Peter Gebhard
 */
public class HWBlock extends Node {

    HWBlock spec;
    HWBlockType type;

    /**
     * @param name
     * @param joints
     * @param annotations
     * @param spec
     * @param type
     */
    public HWBlock(String name, HWBlock spec) {
        super(name, new HashMap<String, Joint>(), spec.getAnnotationsCopy());
        this.spec = spec;
        this.type = spec.type;
        this.features = spec.getJointsCopy(this);
    }

    /**
     * @param name
     * @param type
     */
    public HWBlock(String name, HWBlockType type) {
        super(name, new HashMap<String, Joint>());
        this.type = type;
    }

    /**
     * @return the spec
     */
    @Override
    public HWBlock getSpec() {
        return spec;
    }

    /**
     * @param spec
     *            the spec to set
     */
    public void setSpec(HWBlock spec) {
        this.spec = spec;
    }

    /**
     * @return the type
     */
    public HWBlockType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(HWBlockType type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Joint> getJoints() {
        return (Map<String, Joint>) features;
    }

    private Map<String, Joint> getJointsCopy(HWBlock hwBlock) {
        Map<String, Joint> copy = new HashMap<String, Joint>();
        for (Entry<String, ? extends Feature> e : features.entrySet()) {
            copy.put(e.getKey(), ((Joint) e.getValue()).getClone(e.getKey(), hwBlock));
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public void addJoint(Joint j) {
        ((Map<String, Joint>) this.features).put(j.getName(), j);
    }

    @Override
    public List<Endpoint> getEndpoints() {
        ArrayList<Endpoint> list = new ArrayList<Endpoint>();
        list.addAll(getJoints().values());
        return list;
    }

    @Override
    public HWBlock clone(String name) {
        return new HWBlock(name, this);
    }
}
