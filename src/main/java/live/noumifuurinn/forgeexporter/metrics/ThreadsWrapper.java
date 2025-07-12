package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;

public class ThreadsWrapper extends Metric {
    private final JvmThreadMetrics jvmThreadMetrics = new JvmThreadMetrics();

    public ThreadsWrapper(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public void register() {
        jvmThreadMetrics.bindTo(registry);
    }
}