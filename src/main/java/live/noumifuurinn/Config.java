package live.noumifuurinn;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@me.shedaniel.autoconfig.annotation.Config(name = "fabric-exporter")
public class Config implements ConfigData {
    private transient final List<Runnable> loadListeners = new ArrayList<>();

    public String prefix = "mc.";
    public Map<String, String> tags = Collections.emptyMap();
    public PrometheusConfig prometheus = new PrometheusConfig();
    public MetricsConfig metrics = new MetricsConfig();

    public static class PrometheusConfig {
        public boolean enable = true;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 65535)
        public int port = 9225;

        public String host = "0.0.0.0";

        public String unixSocketPath = "";
    }

    @Getter
    @FieldNameConstants
    public static class MetricsConfig {
        public boolean processor = true;
        public boolean gc = true;
        public boolean entities = true;
        public boolean loadedChunks = true;
        public boolean memory = true;
        public boolean playerOnline = true;
        public boolean playersWorld = true;
        public boolean threads = true;
        public boolean tickDurationAverage = true;
        public boolean tickDurationMax = true;
        public boolean tickDurationMedian = true;
        public boolean tickDurationMin = true;
        public boolean tps = true;
        public boolean worldSize = true;
    }

    @Override
    public void validatePostLoad() {
        for (Runnable loadListener : loadListeners) {
            try {
                loadListener.run();
            } catch (Exception e) {
                log.warn("failed to call reload callback", e);
            }
        }
    }

    public void addReloadCallback(Runnable callback) {
        loadListeners.add(callback);
    }
}