package live.noumifuurinn;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import live.noumifuurinn.metrics.*;
import live.noumifuurinn.utils.LambdaUtils;
import live.noumifuurinn.utils.SerializableFunction;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.util.StringUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.unixdomain.server.UnixDomainServerConnector;

import java.net.InetSocketAddress;
import java.net.UnixDomainSocketAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class MetricsServer {
    private static final Map<SerializableFunction<Config.MetricsConfig, Boolean>, Function<MeterRegistry, Metric>> MERTIC_MAP;

    static {
        MERTIC_MAP = new HashMap<>();

        MERTIC_MAP.put(Config.MetricsConfig::isProcessor, Processor::new);
        MERTIC_MAP.put(Config.MetricsConfig::isGc, GarbageCollectorWrapper::new);
        MERTIC_MAP.put(Config.MetricsConfig::isEntities, Entities::new);
        MERTIC_MAP.put(Config.MetricsConfig::isLoadedChunks, LoadedChunks::new);
        MERTIC_MAP.put(Config.MetricsConfig::isMemory, Memory::new);
        MERTIC_MAP.put(Config.MetricsConfig::isPlayerOnline, PlayerOnline::new);
        MERTIC_MAP.put(Config.MetricsConfig::isPlayersOnlineTotal, PlayersOnlineTotal::new);
        MERTIC_MAP.put(Config.MetricsConfig::isThreads, ThreadsWrapper::new);
        MERTIC_MAP.put(Config.MetricsConfig::isTickDurationAverage, TickDurationAverageCollector::new);
        MERTIC_MAP.put(Config.MetricsConfig::isTickDurationMax, TickDurationMaxCollector::new);
        MERTIC_MAP.put(Config.MetricsConfig::isTickDurationMedian, TickDurationMedianCollector::new);
        MERTIC_MAP.put(Config.MetricsConfig::isTickDurationMin, TickDurationMinCollector::new);
        MERTIC_MAP.put(Config.MetricsConfig::isTps, Tps::new);
        MERTIC_MAP.put(Config.MetricsConfig::isWorldSize, WorldSize::new);
    }

    private final CompositeMeterRegistry registry;
    private final FabricExporter prometheusExporter;
    private final Config config;

    private final Map<SerializableFunction<Config.MetricsConfig, Boolean>, Metric> metrics = new HashMap<>();

    private Server server;

    public MetricsServer(CompositeMeterRegistry registry, FabricExporter prometheusExporter, Config config) {
        this.registry = registry;
        this.prometheusExporter = prometheusExporter;
        this.config = config;

        config.addReloadCallback(this::reloadMeters);
    }

    public void start() {
        if (config.prometheus.enable) {
            startPrometheus(config.prometheus);
        }

        reloadMeters();
    }

    private void reloadMeters() {
        MERTIC_MAP.forEach((metricConf, getter) -> {
            Metric metric = metrics.computeIfAbsent(metricConf, ignore -> getter.apply(registry));
            String metricPath = LambdaUtils.getPropertyName(metricConf);
            if (metricConf.apply(config.metrics)) {
                try {
                    metric.enable();
                    log.info("enable metric {}", metricPath);
                } catch (Exception e) {
                    log.warn("failed to enable metric {}", metricPath, e);
                }
            } else {
                try {
                    metric.disable();
                    log.info("disable metric {}", metricPath);
                } catch (Exception e) {
                    log.warn("failed to disable metric {}", metricPath, e);
                }
            }
        });
    }

    @SneakyThrows
    private void startPrometheus(Config.PrometheusConfig config) {
        PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.add(prometheusMeterRegistry);

        GzipHandler handler = new GzipHandler();
        handler.setHandler(new MetricsController(prometheusExporter, prometheusMeterRegistry));

        if (!StringUtil.isBlank(config.unixSocketPath) && isUnixSocketSupported()) {
            // 使用 Unix Socket
            server = new Server();
            UnixDomainServerConnector connector = new UnixDomainServerConnector(server);
            connector.setUnixDomainPath(Path.of(config.unixSocketPath));
            // 可选：设置其他参数
            connector.setAcceptQueueSize(128);
            connector.setAcceptedReceiveBufferSize(8192);
            connector.setAcceptedSendBufferSize(8192);

            server.addConnector(connector);
            log.info("Started Prometheus metrics endpoint at: " + config.unixSocketPath);
        } else {
            // 使用 TCP Socket
            InetSocketAddress address = new InetSocketAddress(config.host, config.port);
            server = new Server(address);
            log.info("Started Prometheus metrics endpoint at: " + config.host + ":" + config.port);
        }
        server.setHandler(handler);

        server.start();
    }

    public void stop() throws Exception {
        if (server == null) {
            return;
        }
        server.stop();
    }

    private static boolean isUnixSocketSupported() {
        // 检查操作系统
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return false;
        }

        // 尝试创建 Unix Socket 地址
        try {
            UnixDomainSocketAddress.of("/tmp/test.sock");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
