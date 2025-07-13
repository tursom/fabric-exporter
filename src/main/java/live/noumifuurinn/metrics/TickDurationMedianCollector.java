package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TickDurationMedianCollector extends TickDurationCollector {
    private static final String NAME = "tick.duration.median";

    public TickDurationMedianCollector(MeterRegistry registry) {
        super(registry);
    }

    private long getTickDurationMedian() {
        /* Copy the original array - don't want to sort it! */
        long[] tickTimes = getTickDurations().clone();
        Arrays.sort(tickTimes);
        return tickTimes[tickTimes.length / 2];
    }

    @Override
    public Collection<Meter> register() {
        return List.of(Gauge.builder(prefix(NAME), this, TickDurationMedianCollector::getTickDurationMedian)
                .strongReference(true)
                .register(registry));
    }
}
