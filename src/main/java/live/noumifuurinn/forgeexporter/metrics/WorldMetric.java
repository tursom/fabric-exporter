package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.forgeexporter.FabricExporter;
import net.minecraft.server.level.ServerLevel;

public abstract class WorldMetric extends Metric {
    public WorldMetric(MeterRegistry registry) {
        super(registry);
    }

    @Override
    public final void register() {
        for (ServerLevel world : FabricExporter.getServer().getAllLevels()) {
            register(world);
        }
    }


    protected abstract void register(ServerLevel world);
}
