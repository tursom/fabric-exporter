package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;

public class Memory extends Metric {
    private final JvmMemoryMetrics jvmMemoryMetrics = new JvmMemoryMetrics();

    public Memory(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public void register() {
        jvmMemoryMetrics.bindTo(registry);
    }
}
