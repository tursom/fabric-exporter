package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;

import java.util.Collection;

public class GarbageCollectorWrapper extends Metric {
    private final JvmGcMetrics jvmGcMetrics = new JvmGcMetrics();

    public GarbageCollectorWrapper(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public Collection<Meter> register() {
        // 注册所有 JVM 相关指标
        jvmGcMetrics.bindTo(registry);
        return meters;
    }

    @Override
    public void disable() {
        if (!isEnabled()) {
            return;
        }

        throw new UnsupportedOperationException("gc metrics cannot be disable on runtime");
    }
}