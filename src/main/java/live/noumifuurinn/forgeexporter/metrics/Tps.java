package live.noumifuurinn.forgeexporter.metrics;

import live.noumifuurinn.forgeexporter.FabricExporter;
import live.noumifuurinn.forgeexporter.tps.TpsCollector;
import io.prometheus.client.Gauge;

public class Tps extends Metric {

    private static final Gauge TPS = Gauge.build()
            .name(prefix("tps"))
            .help("Server TPS (ticks per second)")
            .create();

    private TpsCollector tpsCollector = new TpsCollector();

    public Tps() {
        super(TPS);
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
    public void doCollect() {
        TPS.set(tpsCollector.getAverageTPS());
    }
}
