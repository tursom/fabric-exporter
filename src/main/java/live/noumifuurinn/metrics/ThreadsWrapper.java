package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;

import java.util.Collection;

public class ThreadsWrapper extends Metric {
    private final JvmThreadMetrics jvmThreadMetrics = new JvmThreadMetrics();

    public ThreadsWrapper(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public Collection<Meter> register() {
        jvmThreadMetrics.bindTo(registry);
        return meters;
    }

    @Override
    public void disable() {
        if (!isEnabled()) {
            return;
        }

        throw new UnsupportedOperationException("thread metrics cannot be disable on runtime");
    }
}