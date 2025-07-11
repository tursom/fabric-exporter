package live.noumifuurinn.forgeexporter.metrics;

import com.sun.management.OperatingSystemMXBean;
import io.prometheus.client.Gauge;

import java.lang.management.ManagementFactory;

public class Processor extends Metric {
    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    public static double cpuLoad() {
        return osmxb.getProcessCpuLoad();
    }

    private static final Gauge CPU = Gauge.build()
            .name(prefix("cpu_usage"))
            .help("CPU Usage")
            .create();

    public Processor() {
        super(CPU);
    }

    @Override
    public void doCollect() {
        CPU.set(cpuLoad());
    }
}
