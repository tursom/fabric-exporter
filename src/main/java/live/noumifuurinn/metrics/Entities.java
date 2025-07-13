package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;

/**
 * Get current count of all entities.
 */
public class Entities extends WorldMetric {
    public Entities(MeterRegistry registry) {
        super(registry);
    }

    @Override
    protected Meter register(ServerLevel world) {
        String name = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();

        return Gauge.builder(prefix("entities.total"), world, Entities::getEntityCount)
                .tag("world", name)
                .tag("mod", mod)
                .register(registry);
    }

    private static double getEntityCount(ServerLevel world) {
        LevelEntityGetterAdapter<Entity> getter = (LevelEntityGetterAdapter<Entity>) world.getEntities();
        return getter.visibleEntities.count();
    }
}
