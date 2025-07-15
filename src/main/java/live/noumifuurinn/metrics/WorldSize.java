package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.utils.CommonUtils;
import live.noumifuurinn.utils.PathFileSize;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerLevel;

import java.nio.file.Path;

public class WorldSize extends WorldMetric {
    public WorldSize(MeterRegistry registry) {
        super(registry);
    }

    @Override
    @SneakyThrows
    protected Meter register(ServerLevel world) {
        String worldName = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();
        return Gauge.builder("world.size", world, w -> {
                    Path path = CommonUtils.getServer().storageSource.getDimensionPath(w.dimension());
                    PathFileSize pathUtils = new PathFileSize(path);
                    return pathUtils.getSize();
                })
                .tag("world", worldName)
                .tag("mod", mod)
                .register(registry);
    }
}
