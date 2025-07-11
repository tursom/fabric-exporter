package live.noumifuurinn.forgeexporter.metrics;

import live.noumifuurinn.forgeexporter.FabricExporter;
import io.prometheus.client.Collector;
import net.minecraft.server.level.ServerLevel;

public abstract class WorldMetric extends Metric {
    public WorldMetric(Collector collector) {
        super(collector);
    }

    @Override
    public final void doCollect() {
        clear();
        for (ServerLevel world : FabricExporter.getServer().getAllLevels()) {
            collect(world);
        }
    }

    protected abstract void clear();
    protected abstract void collect(ServerLevel world);
/*
    protected String getEntityName(EntityType<> type) {
        try {
            return type.getKey().getKey();y
        } catch (Throwable t) {
            // Note: The entity type key above was introduced in 1.14. Older implementations should fallback here.
            return type.name();
        }
    }
    */
}
