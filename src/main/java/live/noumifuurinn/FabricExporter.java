package live.noumifuurinn;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import live.noumifuurinn.utils.CommonUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class FabricExporter implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final CompositeMeterRegistry registry = new CompositeMeterRegistry();
    private static Config config;

    private MetricsServer metricsServer;

    @Override
    public void onInitialize() {
        AutoConfig.register(Config.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(Config.class).getConfig();
        metricsServer = new MetricsServer(registry, this, config);

        // 注册服务器启动事件
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

        ServerLifecycleEvents.SERVER_STOPPING.register(this::stop);

        LOGGER.info("FabricExporter initialized");
    }

    public void onServerStarted(MinecraftServer server) {
        if (!StringUtil.isNullOrEmpty(config.prefix)) {
            registry.config().meterFilter(new MeterFilter() {
                @Override
                public Meter.@NotNull Id map(Meter.@NotNull Id id) {
                    return id.withName(config.prefix + id.getName());
                }
            });
        }
        if (!config.tags.isEmpty()) {
            registry.config().commonTags(config.tags.entrySet().stream()
                    .map((entry) -> Tag.of(entry.getKey(), entry.getValue()))
                    .toList());
        }

        CommonUtils.setServer(server);
        metricsServer.start();
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public void stop(MinecraftServer ignore) {
        try {
            metricsServer.stop();
        } catch (Exception e) {
            LOGGER.warn("Failed to stop metrics server gracefully", e);
        }
    }
}
