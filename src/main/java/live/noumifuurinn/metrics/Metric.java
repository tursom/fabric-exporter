package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;

@Slf4j
public abstract class Metric {
    protected final MeterRegistry registry;
    @Getter
    protected boolean enabled = false;

    protected Collection<Meter> meters = Collections.emptyList();

    public Metric(MeterRegistry registry) {
        this.registry = registry;
    }

    public abstract Collection<Meter> register();

    public void enable() {
        if (enabled) {
            return;
        }

        enabled = true;
        meters = register();
    }

    public void disable() {
        if (!enabled) {
            return;
        }

        enabled = false;
        for (Meter meter : meters) {
            try {
                registry.remove(meter);
            } catch (Exception e) {
                log.warn("Failed to remove meter: {}", meter.getId(), e);
            }
        }

        meters = Collections.emptyList();
    }
}
