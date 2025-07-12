package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;

import java.util.List;

/**
 * Get current count of all entities.
 */
public class Entities extends WorldMetric {
    public Entities(MeterRegistry registry) {
        super(registry);
    }

    @Override
    protected void register(ServerLevel world) {
        String name = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();
        LevelEntityGetterAdapter<Entity> getter = (LevelEntityGetterAdapter<Entity>) world.getEntities();
        registry.gauge(
                prefix("entities.total"),
                List.of(Tag.of("world", name), Tag.of("mod", mod)),
                getter.visibleEntities, EntityLookup::count
        );
    }
}
