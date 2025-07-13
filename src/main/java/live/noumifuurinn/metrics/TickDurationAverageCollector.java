package live.noumifuurinn.metrics;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Collection;
import java.util.List;

public class TickDurationAverageCollector extends TickDurationCollector {
    private static final String NAME = "tick.duration.average";


    public TickDurationAverageCollector(MeterRegistry registry) {
        super(registry);
    }

    private long getTickDurationAverage() {
        long sum = 0;
        long[] durations = getTickDurations();
        for (Long val : durations) {
            sum += val;
        }
        return sum / durations.length;
    }

    @Override
    public Collection<Meter> register() {
        return List.of(Gauge.builder(prefix(NAME), this, TickDurationAverageCollector::getTickDurationAverage)
                .strongReference(true)
                .register(registry));
    }
}
