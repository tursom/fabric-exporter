package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;

public class Processor extends Metric {
    private static final ProcessorMetrics PROCESSOR_METRICS = new ProcessorMetrics();

    public Processor(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public void register() {
        PROCESSOR_METRICS.bindTo(registry);
    }
}
