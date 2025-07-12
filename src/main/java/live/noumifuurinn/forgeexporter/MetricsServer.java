package live.noumifuurinn.forgeexporter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import live.noumifuurinn.forgeexporter.metrics.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.util.StringUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.unixdomain.server.UnixDomainServerConnector;

import java.net.InetSocketAddress;
import java.net.UnixDomainSocketAddress;
import java.nio.file.Path;

@Slf4j
public class MetricsServer {
    private final CompositeMeterRegistry registry;
    private final FabricExporter prometheusExporter;
    private final FabricExporterConfig config;

    private Server server;

    public MetricsServer(CompositeMeterRegistry registry, FabricExporter prometheusExporter, FabricExporterConfig config) {
        this.registry = registry;
        this.prometheusExporter = prometheusExporter;
        this.config = config;
    }

    public void start() {
        if (config.prometheus.enable) {
            startPrometheus(config.prometheus);
        }

        new Processor(registry).enable();
        new GarbageCollectorWrapper(registry).enable();
        new Entities(registry).enable();
        new LoadedChunks(registry).enable();
        new Memory(registry).enable();
        new PlayerOnline(registry).enable();
        new PlayersOnlineTotal(registry).enable();
        new ThreadsWrapper(registry).enable();
        new TickDurationAverageCollector(registry).enable();
        new TickDurationMaxCollector(registry).enable();
        new TickDurationMedianCollector(registry).enable();
        new TickDurationMinCollector(registry).enable();
        new Tps(registry).enable();
        new WorldSize(registry).enable();

    }

    @SneakyThrows
    private void startPrometheus(FabricExporterConfig.PrometheusConfig config) {
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
