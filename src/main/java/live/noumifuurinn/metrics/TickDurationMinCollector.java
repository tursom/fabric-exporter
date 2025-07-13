package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Collection;
import java.util.List;

public class TickDurationMinCollector extends TickDurationCollector {
    private static final String NAME = "tick.duration.min";

    public TickDurationMinCollector(MeterRegistry registry) {
        super(registry);
    }

    private long getTickDurationMin() {
        long min = Long.MAX_VALUE;
        for (long val : getTickDurations()) {
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    @Override
    public Collection<Meter> register() {
        return List.of(Gauge.builder(prefix(NAME), this, TickDurationMinCollector::getTickDurationMin)
                .strongReference(true)
                .register(registry));
    }
}

