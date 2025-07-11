package live.noumifuurinn.forgeexporter.metrics;

import io.prometheus.client.Gauge;

import java.util.Arrays;

public class TickDurationMedianCollector extends TickDurationCollector {
    private static final String NAME = "tick_duration_median";

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Median duration of server tick (nanoseconds)")
            .create();

    public TickDurationMedianCollector() {
        super(TD, NAME);
    }

    private long getTickDurationMedian() {
        /* Copy the original array - don't want to sort it! */
        long[] tickTimes = getTickDurations().clone();
        Arrays.sort(tickTimes);
        return tickTimes[tickTimes.length / 2];
    }

    @Override
    public void doCollect() {
        TD.set(getTickDurationMedian());
    }
}
