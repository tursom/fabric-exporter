package live.noumifuurinn.forgeexporter.metrics;

import live.noumifuurinn.forgeexporter.FabricExporter;
import live.noumifuurinn.forgeexporter.utils.PathFileSize;
import io.prometheus.client.Gauge;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class WorldSize extends WorldMetric {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Gauge WORLD_SIZE = Gauge.build()
            .name(prefix("world_size"))
            .help("World size in bytes")
            .labelNames("world", "mod")
            .create();

    public WorldSize() {
        super(WORLD_SIZE);
    }

    @Override
    protected void clear() {
        WORLD_SIZE.clear();
    }

    @Override
    public void collect(ServerLevel world) {
        try {
            Path path = FabricExporter.getServer().storageSource.getDimensionPath(world.dimension());
            PathFileSize pathUtils = new PathFileSize(path);
            long size = pathUtils.getSize();
            String worldName = world.dimension().location().getPath();
            String mod = world.dimension().location().getNamespace();
            WORLD_SIZE.labels(worldName, mod).set(size);
        } catch (Throwable t) {
            LOGGER.error("collect", t);
        }
    }
}
