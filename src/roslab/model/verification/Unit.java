/**
 *
 */
package roslab.model.verification;

/**
 * @author Peter Gebhard
 */
public class Unit {

    MetricPrefix prefix;
    BaseUnit unit;

    /**
     */
    private Unit() {
        this.prefix = MetricPrefix.None;
        this.unit = BaseUnit.None;
    }

    /**
     * @param prefix
     * @param unit
     */
    public Unit(MetricPrefix prefix, BaseUnit unit) {
        this.prefix = prefix;
        this.unit = unit;
    }

    /**
     * @return the prefix
     */
    public MetricPrefix getPrefix() {
        return prefix;
    }

    /**
     * @param prefix
     *            the prefix to set
     */
    private void setPrefix(MetricPrefix prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the unit
     */
    public BaseUnit getUnit() {
        return unit;
    }

    /**
     * @param unit
     *            the unit to set
     */
    private void setUnit(BaseUnit unit) {
        this.unit = unit;
    }

    public static Unit valueOf(String s) {
        Unit result = new Unit();

        for (MetricPrefix mp : MetricPrefix.values()) {
            if (s.toLowerCase().startsWith(mp.toString().toLowerCase())) {
                result.setPrefix(mp);
            }
        }

        for (BaseUnit bu : BaseUnit.values()) {
            if (s.toLowerCase().endsWith(bu.toString().toLowerCase())) {
                result.setUnit(bu);
            }
        }

        return result;
    }

}
