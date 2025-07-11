package live.noumifuurinn.forgeexporter;

import live.noumifuurinn.forgeexporter.metrics.Metric;

import java.util.ArrayList;
import java.util.List;

public class MetricRegistry {

    private static final MetricRegistry INSTANCE = new MetricRegistry();

    private final List<Metric> metrics = new ArrayList<>();

    private MetricRegistry() {

    }

    public static MetricRegistry getInstance() {
        return INSTANCE;
    }

    public void register(Metric metric) {
        metric.enable();
        this.metrics.add(metric);
    }

    void collectMetrics() {
        this.metrics.forEach(Metric::collect);
    }

}
