package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import lombok.NonNull;

public class ThreadsWrapper extends BinderMetric {
    private final JvmThreadMetrics jvmThreadMetrics = new JvmThreadMetrics();

    public ThreadsWrapper(MeterRegistry registry) {
        super(registry);
    }

    @Override
    protected @NonNull MeterBinder meterBinder() {
        return jvmThreadMetrics;
    }

}