package live.noumifuurinn.forgeexporter;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "fabric-exporter")
public class FabricExporterConfig implements ConfigData {
    public PrometheusConfig prometheus = new PrometheusConfig();

    public static class PrometheusConfig {
        public boolean enable = true;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 65535)
        public int port = 9225;

        public String host = "0.0.0.0";

        public String unixSocketPath = "";
    }
}