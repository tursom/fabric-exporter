package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import net.minecraft.server.level.ServerLevel;

public class LoadedChunks extends WorldMetric {
    public LoadedChunks(MeterRegistry registry) {
        super(registry);
    }

    @Override
    protected Meter register(ServerLevel world) {
        String name = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();

        return Gauge.builder(prefix("loaded.chunks.total"), world, w -> w.getChunkSource().getLoadedChunksCount())
                .tag("world", name)
                .tag("mod", mod)
                .register(registry);
    }
}
