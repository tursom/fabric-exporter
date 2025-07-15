package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import lombok.NonNull;

public class GarbageCollectorWrapper extends BinderMetric {
    private final JvmGcMetrics jvmGcMetrics = new JvmGcMetrics();

    public GarbageCollectorWrapper(MeterRegistry registry) {
        super(registry);

        // 防止 ClassNotFoundException
        registry.remove(registry.timer("example"));
    }

    @Override
    protected @NonNull MeterBinder meterBinder() {
        return jvmGcMetrics;
    }
}