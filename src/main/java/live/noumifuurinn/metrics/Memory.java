package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import lombok.NonNull;

public class Memory extends BinderMetric {
    private final JvmMemoryMetrics jvmMemoryMetrics = new JvmMemoryMetrics();

    public Memory(MeterRegistry registry) {
        super(registry);
    }

    @Override
    protected @NonNull MeterBinder meterBinder() {
        return jvmMemoryMetrics;
    }
}
