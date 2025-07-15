package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TickDurationCollector extends Metric {
    private static final Logger LOGGER = LogManager.getLogger();
    /*
     * If reflection is successful, this will hold a reference directly to the
     * MinecraftServer internal tick duration tracker
     */
    private static long[] tickDurationReference = null;

    public TickDurationCollector(MeterRegistry registry) {
        super(registry);

        /*
         * If there is not yet a handle to the internal tick duration buffer, try
         * to acquire one using reflection.
         *
         * This searches for any long[] array in the MinecraftServer class. It should
         * work across many versions of Spigot/Paper and various obfuscation mappings
         */
        if (tickDurationReference == null) {
            long[] longestArray = CommonUtils.getTickTimesNanos();

            if (longestArray != null) {
                tickDurationReference = longestArray;
            } else {
                /* No array was found, use an placeholder */
                tickDurationReference = new long[1];
                tickDurationReference[0] = -1;

                LOGGER.warn("Failed to find tick times buffer via reflection. Tick duration metrics will not be available.");
            }
        }
    }

    /**
     * Returns either the internal minecraft long array for tick times in ns,
     * or a long array containing just one element of value -1 if reflection
     * was unable to locate the minecraft tick times buffer
     */
    protected static long[] getTickDurations() {
        return tickDurationReference;
    }
}
