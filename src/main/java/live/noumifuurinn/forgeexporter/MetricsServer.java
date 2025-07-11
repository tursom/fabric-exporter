package live.noumifuurinn.forgeexporter;

import live.noumifuurinn.forgeexporter.metrics.*;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MetricsServer {

    private final String host;
    private final int port;
    private final FabricExporter prometheusExporter;

    private Server server;

    public MetricsServer(String host, int port, FabricExporter prometheusExporter) {
        this.host = host;
        this.port = port;
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

        InetSocketAddress address = new InetSocketAddress(host, port);
        server = new Server(address);
        server.setHandler(handler);

        server.start();
    }

    public void stop() throws Exception {
        if (server == null) {
            return;
        }
        server.stop();
    }
}
