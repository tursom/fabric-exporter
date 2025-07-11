package live.noumifuurinn.forgeexporter.metrics;


import io.prometheus.client.Gauge;

public class TickDurationAverageCollector extends TickDurationCollector {
    private static final String NAME = "tick_duration_average";

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Average duration of server tick (nanoseconds)")
            .create();

    public TickDurationAverageCollector() {
        super(TD, NAME);
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
    public void doCollect() {
        TD.set(getTickDurationAverage());
    }
}
