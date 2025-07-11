package live.noumifuurinn.forgeexporter.metrics;

import com.sun.management.OperatingSystemMXBean;
import io.prometheus.client.Gauge;

import java.lang.management.ManagementFactory;

public class Memory extends Metric {

    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    public static int cpuLoad() {

        double cpuLoad = osmxb.getProcessCpuLoad();

        int percentCpuLoad = (int) (cpuLoad * 100);

        return percentCpuLoad;

    }

    private static final Gauge MEMORY = Gauge.build()
            .name(prefix("jvm_memory"))
            .help("JVM memory usage")
            .labelNames("type")
            .create();

    public Memory() {
        super(MEMORY);
    }

    @Override
    public void doCollect() {
        MEMORY.labels("max").set(Runtime.getRuntime().maxMemory());
        MEMORY.labels("free").set(Runtime.getRuntime().freeMemory());
        MEMORY.labels("allocated").set(Runtime.getRuntime().totalMemory());
    }
}
