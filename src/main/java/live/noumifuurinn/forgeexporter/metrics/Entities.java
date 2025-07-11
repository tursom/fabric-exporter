package live.noumifuurinn.forgeexporter.metrics;

import io.prometheus.client.Gauge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;

/**
 * Get current count of all entities.
 */
public class Entities extends WorldMetric {

    private static final Gauge ENTITIES = Gauge.build()
            .name(prefix("entities_total"))
            .help("Entities loaded per world")
            .labelNames("world", "mod")
            .create();

    public Entities() {
        super(ENTITIES);
    }

    @Override
    protected void clear() {

    }

    @Override
    public void collect(ServerLevel world) {
        String name = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();
        LevelEntityGetterAdapter<Entity> getter = (LevelEntityGetterAdapter<Entity>) world.getEntities();
        ENTITIES.labels(name, mod).set(getter.visibleEntities.count());
    }
}
