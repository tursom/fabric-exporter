package live.noumifuurinn.forgeexporter.metrics;


import io.micrometer.core.instrument.MeterRegistry;

public class TickDurationMinCollector extends TickDurationCollector {
    private static final String NAME = "tick.duration.min";

    public TickDurationMinCollector(MeterRegistry registry) {
        super(registry);
    }

    private long getTickDurationMin() {
        long min = Long.MAX_VALUE;
        for (Long val : getTickDurations()) {
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    @Override
    public void register() {
        io.micrometer.core.instrument.Gauge.builder(prefix(NAME), this, TickDurationMinCollector::getTickDurationMin)
                .register(registry);
    }
}

