package live.noumifuurinn.forgeexporter;

import live.noumifuurinn.forgeexporter.metrics.*;
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

    private final String host;
    private final int port;
    private final String unixSocketPath;
    private final FabricExporter prometheusExporter;

    private Server server;

    public MetricsServer(String host, int port, String unixSocketPath, FabricExporter prometheusExporter) {
        this.host = host;
        this.port = port;
        this.unixSocketPath = unixSocketPath;
        this.prometheusExporter = prometheusExporter;
    }

    public void start() throws Exception {
        MetricRegistry.getInstance().register(new Processor());
        MetricRegistry.getInstance().register(new GarbageCollectorWrapper());
        MetricRegistry.getInstance().register(new Entities());
        MetricRegistry.getInstance().register(new LoadedChunks());
        MetricRegistry.getInstance().register(new Memory());
        MetricRegistry.getInstance().register(new PlayerOnline());
        MetricRegistry.getInstance().register(new PlayersOnlineTotal());
        MetricRegistry.getInstance().register(new ThreadsWrapper());
        MetricRegistry.getInstance().register(new TickDurationAverageCollector());
        MetricRegistry.getInstance().register(new TickDurationMaxCollector());
        MetricRegistry.getInstance().register(new TickDurationMedianCollector());
        MetricRegistry.getInstance().register(new TickDurationMinCollector());
        MetricRegistry.getInstance().register(new Tps());
        MetricRegistry.getInstance().register(new WorldSize());

        GzipHandler handler = new GzipHandler();
        handler.setHandler(new MetricsController(prometheusExporter));

        if (!StringUtil.isBlank(unixSocketPath) && isUnixSocketSupported()) {
            // 使用 Unix Socket
            server = new Server();
            UnixDomainServerConnector connector = new UnixDomainServerConnector(server);
            connector.setUnixDomainPath(Path.of(unixSocketPath));
            // 可选：设置其他参数
            connector.setAcceptQueueSize(128);
            connector.setAcceptedReceiveBufferSize(8192);
            connector.setAcceptedSendBufferSize(8192);

            server.addConnector(connector);
            log.info("Started Prometheus metrics endpoint at: " + unixSocketPath);
        } else {
            // 使用 TCP Socket
            InetSocketAddress address = new InetSocketAddress(host, port);
            server = new Server(address);
            log.info("Started Prometheus metrics endpoint at: " + host + ":" + port);
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
