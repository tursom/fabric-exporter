package live.noumifuurinn;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class MetricsController extends Handler.Abstract {
    private final FabricExporter exporter;
    private final PrometheusMeterRegistry registry;

    public MetricsController(FabricExporter exporter, PrometheusMeterRegistry registry) {
        this.exporter = exporter;
        this.registry = registry;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        if (!request.getHttpURI().getPath().equals("/metrics")) {
            response.setStatus(HttpStatus.NOT_FOUND_404);
            callback.succeeded();
            return true;
        }

        try {
            response.setStatus(HttpStatus.OK_200);
            response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/plain; version=0.0.4; charset=utf-8");
            response.getHeaders().put(HttpHeader.CONTENT_ENCODING, "UTF-8");

            // 使用 OutputStreamWriter 写入响应
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    Response.asBufferedOutputStream(request, response),
                    StandardCharsets.UTF_8)) {
                writer.append(registry.scrape());
            }
            callback.succeeded();
        } catch (Throwable e) {
            exporter.getLogger().warn("Failed to read server statistic: " + e.getMessage());
            exporter.getLogger().warn("Failed to read server statistic: ", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            callback.failed(e);
        }

        return true;
    }
}
