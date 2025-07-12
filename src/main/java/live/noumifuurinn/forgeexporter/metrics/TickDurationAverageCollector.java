package live.noumifuurinn.forgeexporter.metrics;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

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
    public void register() {
        Gauge.builder(prefix(NAME), this, TickDurationAverageCollector::getTickDurationAverage)
                .register(registry);
    }
}
