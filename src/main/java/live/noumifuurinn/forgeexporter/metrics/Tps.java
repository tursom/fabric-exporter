package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.forgeexporter.FabricExporter;
import live.noumifuurinn.forgeexporter.tps.TpsCollector;

public class Tps extends Metric {
    private final TpsCollector tpsCollector = new TpsCollector();

    public Tps(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public void enable() {
        super.enable();
        FabricExporter.registerServerTickEvent(this, tpsCollector);
    }

    @Override
    public void disable() {
        super.disable();
        FabricExporter.unregisterServerTickEvent(this);
    }

    @Override
    public void register() {
        Gauge.builder(prefix("tps"), tpsCollector, TpsCollector::getAverageTPS)
                .description("Server TPS (ticks per second)")
                .register(registry);
    }
}
