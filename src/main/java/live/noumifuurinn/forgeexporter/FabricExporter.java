package live.noumifuurinn.forgeexporter;

import lombok.SneakyThrows;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class FabricExporter implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static MinecraftServer mcServer;
    private final static Map<Object, Runnable> serverTickReg = new java.util.concurrent.ConcurrentHashMap<>();

    private static FabricExporterConfig config;
    private MetricsServer server;

    @Override
    public void onInitialize() {
        AutoConfig.register(FabricExporterConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(FabricExporterConfig.class).getConfig();

        // 注册服务器启动事件
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

        ServerLifecycleEvents.SERVER_STOPPING.register(this::stop);

        // 注册服务器 tick 事件
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);

        LOGGER.info("FabricExporter initialized");
    }

    public void onServerStarted(MinecraftServer server) {
        mcServer = server;

        startMetricsServer();
    }

    private void onServerTick(MinecraftServer server) {
        for (Runnable r : serverTickReg.values()) {
            try {
                r.run();
            } catch (Throwable t) {
                LOGGER.warn("Error in server tick event", t);
            }
        }
    }

    public static void registerServerTickEvent(Object parent, Runnable r) {
        serverTickReg.put(parent, r);
    }

    public static void unregisterServerTickEvent(Object parent) {
        serverTickReg.remove(parent);
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public static MinecraftServer getServer() {
        return mcServer;
    }

    @SneakyThrows
    private void startMetricsServer() {
        String host = config.host;
        Integer port = config.port;
        String unixSocketPath = config.unixSocketPath;

        server = new MetricsServer(host, port, unixSocketPath, this);
        server.start();
    }

    public void stop(MinecraftServer ignore) {
        try {
            server.stop();
        } catch (Exception e) {
            LOGGER.warn("Failed to stop metrics server gracefully: " + e.getMessage());
            LOGGER.warn("Failed to stop metrics server gracefully", e);
        }
    }
}
