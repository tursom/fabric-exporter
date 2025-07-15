package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

public abstract class BinderMetric extends Metric {
    public BinderMetric(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public final Collection<Meter> register() {
        meterBinder().bindTo(registry);
        return List.of();
    }

    @Override
    public final void disable() {
        if (!isEnabled()) {
            return;
        }

        throw new UnsupportedOperationException("processor metrics cannot be disable on runtime");
    }

    @NonNull
    abstract protected MeterBinder meterBinder();
}
