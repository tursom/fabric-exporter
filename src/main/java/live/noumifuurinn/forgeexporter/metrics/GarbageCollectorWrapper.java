package live.noumifuurinn.forgeexporter.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.hotspot.GarbageCollectorExports;

import java.util.List;

public class GarbageCollectorWrapper extends Metric {
    public GarbageCollectorWrapper() {
        super(new GarbageCollectorExportsCollector());
    }

    @Override
    protected void doCollect() {}

    private static class GarbageCollectorExportsCollector extends Collector {
        private static final GarbageCollectorExports garbageCollectorExports = new GarbageCollectorExports();

        @Override
        public List<MetricFamilySamples> collect() {
            return HotspotPrefixer.prefixFromCollector(garbageCollectorExports);
        }
    }
}