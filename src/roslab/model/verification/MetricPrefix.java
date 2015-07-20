package roslab.model.verification;

public enum MetricPrefix {
    Giga(1e9), Mega(1e6), Kilo(1e3), Hecto(1e2), Deca(1e1), None, Deci(1e-1), Centi(1e-2), Milli(1e-3), Micro(1e-6), Nano(1e-9), Pico(1e-12);

    private Double scaleFactor;

    /**
     */
    MetricPrefix() {
        this.scaleFactor = 1.0;
    }

    /**
     * @param scaleFactor
     */
    MetricPrefix(Double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * @return the scaleFactor
     */
    public Double getScaleFactor() {
        return scaleFactor;
    }

}
