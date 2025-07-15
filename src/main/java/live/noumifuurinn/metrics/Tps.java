package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.tps.TpsCollector;
import live.noumifuurinn.utils.CommonUtils;

import java.util.Collection;
import java.util.List;

public class Tps extends Metric {
    private final TpsCollector tpsCollector = new TpsCollector();

    public Tps(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public void enable() {
        super.enable();
        CommonUtils.registerServerTickEvent(this, tpsCollector);
    }

    @Override
    public void disable() {
        super.disable();
        CommonUtils.unregisterServerTickEvent(this);
    }

    @Override
    public Collection<Meter> register() {
        return List.of(Gauge.builder("tps", tpsCollector, TpsCollector::getAverageTPS)
                .description("Server TPS (ticks per second)")
                .register(registry));
    }
}
