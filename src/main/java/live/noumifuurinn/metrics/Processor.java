package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import lombok.NonNull;

public class Processor extends BinderMetric {
    private static final ProcessorMetrics PROCESSOR_METRICS = new ProcessorMetrics();

    public Processor(MeterRegistry registry) {
        super(registry);
    }

    @Override
    protected @NonNull MeterBinder meterBinder() {
        return PROCESSOR_METRICS;
    }
}
