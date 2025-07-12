package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.Arrays;

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
    public void register() {
        io.micrometer.core.instrument.Gauge.builder(prefix(NAME), this, TickDurationMedianCollector::getTickDurationMedian)
                .strongReference(true)
                .register(registry);
    }
}
