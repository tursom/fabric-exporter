package live.noumifuurinn.forgeexporter.metrics;

import io.prometheus.client.Gauge;
import net.minecraft.server.level.ServerLevel;

public class LoadedChunks extends WorldMetric {

    private static final Gauge LOADED_CHUNKS = Gauge.build()
            .name(prefix("loaded_chunks_total"))
            .help("Chunks loaded per world")
            .labelNames("world", "mod")
            .create();

    public LoadedChunks() {
        super(LOADED_CHUNKS);
    }

    @Override
    protected void clear() {
		LOADED_CHUNKS.clear();
    }

    @Override
    public void collect(ServerLevel world) {
        String name = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();
        LOADED_CHUNKS.labels(name, mod).set(world.getChunkSource().getLoadedChunksCount());
    }
}
