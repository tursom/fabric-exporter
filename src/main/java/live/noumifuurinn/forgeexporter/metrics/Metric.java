package live.noumifuurinn.forgeexporter.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Metric {
    private static final Logger LOGGER = LogManager.getLogger();

    private final static String COMMON_PREFIX = "mc_";

    private final Collector collector;

    private boolean enabled = false;

    protected Metric(Collector collector) {
        this.collector = collector;
    }

    public void collect() {

        if (!enabled) {
            return;
        }

        try {
            doCollect();
        } catch (Exception e) {
            logException(e);
        }
    }

    protected abstract void doCollect();

    private void logException(Exception e) {
        LOGGER.error("collect", e);
    }

    protected static String prefix(String name) {
        return COMMON_PREFIX + name;
    }

    public void enable() {
        CollectorRegistry.defaultRegistry.register(collector);
        enabled = true;
    }

    public void disable() {
        CollectorRegistry.defaultRegistry.unregister(collector);
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
