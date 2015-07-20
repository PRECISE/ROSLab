/**
 *
 */
package roslab.model.verification;

/**
 * @author Peter Gebhard
 */
public class Value {

    Double value;
    Unit unit;

    /**
     * @param value
     * @param unit
     */
    public Value(String value, String unit) {
        this.value = Double.valueOf(value);
        this.unit = Unit.valueOf(unit);
    }

    /**
     * @param value
     * @param unit
     */
    public Value(Double value, String unit) {
        this.value = value;
        this.unit = Unit.valueOf(unit);
    }

    /**
     * @param value
     * @param unit
     */
    public Value(Double value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    /**
     * @param value
     * @param mp
     * @param bu
     */
    public Value(Double value, MetricPrefix mp, BaseUnit bu) {
        this.value = value;
        this.unit = new Unit(mp, bu);
    }

}
