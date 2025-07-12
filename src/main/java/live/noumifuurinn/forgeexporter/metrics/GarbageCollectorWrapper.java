package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;

public class GarbageCollectorWrapper extends Metric {
    private final JvmGcMetrics jvmGcMetrics = new JvmGcMetrics();

    public GarbageCollectorWrapper(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public void register() {
        // 注册所有 JVM 相关指标
        jvmGcMetrics.bindTo(registry);
    }
}