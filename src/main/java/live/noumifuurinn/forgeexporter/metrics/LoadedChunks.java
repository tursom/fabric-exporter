package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class LoadedChunks extends WorldMetric {
    public LoadedChunks(MeterRegistry registry) {
        super(registry);
    }

    @Override
    protected void register(ServerLevel world) {
        String name = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();
        registry.gauge(
                prefix("loaded.chunks.total"),
                List.of(
                        Tag.of("world", name),
                        Tag.of("mod", mod)
                ),
                world.getChunkSource(), ServerChunkCache::getLoadedChunksCount
        );
    }
}
