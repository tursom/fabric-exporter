package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;

public class TickDurationMaxCollector extends TickDurationCollector {
    private static final String NAME = "tick.duration.max";

    public TickDurationMaxCollector(MeterRegistry registry) {
        super(registry);
    }

    private long getTickDurationMax() {
        long max = Long.MIN_VALUE;
        for (Long val : getTickDurations()) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    @Override
    public void register() {
        io.micrometer.core.instrument.Gauge.builder(prefix(NAME), this, TickDurationMaxCollector::getTickDurationMax)
                .strongReference(true)
                .register(registry);
    }
}

