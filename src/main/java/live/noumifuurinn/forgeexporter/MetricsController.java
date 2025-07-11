package live.noumifuurinn.forgeexporter;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MetricsController extends AbstractHandler {
    private final MetricRegistry metricRegistry = MetricRegistry.getInstance();
    private final FabricExporter exporter;

    public MetricsController(FabricExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
            HttpServletResponse response) throws IOException {

        if (!target.equals("/metrics")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            exporter.getServer().executeBlocking(() -> {
                metricRegistry.collectMetrics();
            });

            response.setStatus(HttpStatus.OK_200);
            response.setContentType(TextFormat.CONTENT_TYPE_004);
            response.setCharacterEncoding("UTF-8");

            TextFormat.write004(response.getWriter(), CollectorRegistry.defaultRegistry.metricFamilySamples());

            request.setHandled(true);
        } catch (Throwable e) {
            exporter.getLogger().warn("Failed to read server statistic: " + e.getMessage());
            exporter.getLogger().warn("Failed to read server statistic: ", e);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }
}
