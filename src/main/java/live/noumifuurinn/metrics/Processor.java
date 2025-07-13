package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;

import java.util.Collection;

public class Processor extends Metric {
    private static final ProcessorMetrics PROCESSOR_METRICS = new ProcessorMetrics();

    public Processor(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public Collection<Meter> register() {
        PROCESSOR_METRICS.bindTo(registry);
        return meters;
    }

    @Override
    public void disable() {
        if (!isEnabled()) {
            return;
        }

        throw new UnsupportedOperationException("processor metrics cannot be disable on runtime");
    }
}
