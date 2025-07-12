package live.noumifuurinn.forgeexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Metric {
    private static final Logger LOGGER = LogManager.getLogger();

    private final static String COMMON_PREFIX = "mc.";

    protected final MeterRegistry registry;
    private boolean enabled = false;

    public Metric(MeterRegistry registry) {
        this.registry = registry;
    }

    public abstract void register();

    private void logException(Exception e) {
        LOGGER.error("collect", e);
    }

    protected static String prefix(String name) {
        return COMMON_PREFIX + name;
    }

    public void enable() {
        // TODO
        enabled = true;
        register();
    }

    public void disable() {
        // TODO
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
