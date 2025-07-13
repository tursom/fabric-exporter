package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.FabricExporter;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerLevel;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class WorldMetric extends Metric {
    private final ConcurrentMap<SoftReference<ServerLevel>, Meter> worldMeters = new ConcurrentHashMap<>();

    public WorldMetric(MeterRegistry registry) {
        super(registry);

        Thread thread = Thread.ofVirtual().unstarted(this::syncWorldsTask);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public final Collection<Meter> register() {
        return syncWorlds();
    }

    private Collection<Meter> syncWorlds() {
        if (!isEnabled()) {
            return meters;
        }

        var meters = new ArrayList<Meter>();
        for (ServerLevel world : FabricExporter.getServer().getAllLevels()) {
            var meter = worldMeters.computeIfAbsent(new SoftReference<>(world), ref -> this.register(world));
            meters.add(meter);
        }
        return meters;
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    @SneakyThrows
    private void syncWorldsTask() {
        while (true) {
            // 十分钟同步一次世界列表
            Thread.sleep(600_000);

            // 检查是否有世界被卸载
            worldMeters.forEach((world, meter) -> {
                if (world.get() != null) {
                    return;
                }

                registry.remove(meter);
            });

            // 加载新创建的新世界
            syncWorlds();
        }
    }

    protected abstract Meter register(ServerLevel world);
}
