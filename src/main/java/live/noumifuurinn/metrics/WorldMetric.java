package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.utils.CommonUtils;
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
    }

    @Override
    public final Collection<Meter> register() {
        var meters = new ArrayList<Meter>();
        for (ServerLevel world : CommonUtils.getServer().getAllLevels()) {
            var meter = worldMeters.computeIfAbsent(new SoftReference<>(world), ref -> this.register(world));
            meters.add(meter);
        }
        return meters;
    }

    protected abstract Meter register(ServerLevel world);
}
