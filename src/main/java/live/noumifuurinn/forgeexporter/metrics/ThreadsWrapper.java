package live.noumifuurinn.forgeexporter.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.hotspot.ThreadExports;

import java.util.List;

public class ThreadsWrapper extends Metric {
    public ThreadsWrapper() {
        super(new ThreadExportsCollector());
    }

    @Override
    protected void doCollect() {}

    private static class ThreadExportsCollector extends Collector {
        private static final ThreadExports threadExports = new ThreadExports();

        @Override
        public List<MetricFamilySamples> collect() {
            return HotspotPrefixer.prefixFromCollector(threadExports);
        }
    }
}