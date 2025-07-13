package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;

import java.util.Collection;

public class Memory extends Metric {
    private final JvmMemoryMetrics jvmMemoryMetrics = new JvmMemoryMetrics();

    public Memory(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public Collection<Meter> register() {
        jvmMemoryMetrics.bindTo(registry);
        return meters;
    }

    @Override
    public void disable() {
        if (!isEnabled()) {
            return;
        }

        throw new UnsupportedOperationException("memory metrics cannot be disable on runtime");
    }
}
