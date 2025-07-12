package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.forgeexporter.FabricExporter;
import live.noumifuurinn.forgeexporter.utils.PathFileSize;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class WorldSize extends WorldMetric {
    private static final Logger LOGGER = LogManager.getLogger();

    public WorldSize(MeterRegistry registry) {
        super(registry);
    }

    @Override
    @SneakyThrows
    protected void register(ServerLevel world) {
        Path path = FabricExporter.getServer().storageSource.getDimensionPath(world.dimension());
        PathFileSize pathUtils = new PathFileSize(path);
        String worldName = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();
        Gauge.builder(prefix("world.size"), pathUtils, PathFileSize::getSize)
                .tag("world", worldName)
                .tag("mod", mod)
                .register(registry);
    }
}
