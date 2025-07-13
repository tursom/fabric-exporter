package live.noumifuurinn.tps;

import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * Polls the server and maintains a queue of recent TPS values.
 * Inspired by LagMeter.
 *
 * @see <a href="https://github.com/TheLunarFrog/LagMeter">LagMeter</a>
 */
public class TpsCollector implements Runnable {

    /**
     * Max amount of ticks that should happen per second
     */
    static final int TICKS_PER_SECOND = 20;
    /**
     * Every 40 ticks (2s ideally) the server will be polled
     */
    public static final int POLL_INTERVAL = 40;
    /**
     * The amount of TPS values to keep for calculating the average
     */
    static final int TPS_QUEUE_SIZE = 10;

    final private Supplier<Long> systemTimeSupplier;
    private LinkedList<Float> tpsQueue = new LinkedList<>();
    private long lastPoll;
    private int ticks;

    public TpsCollector() {
        this(System::currentTimeMillis);
    }

    public TpsCollector(Supplier<Long> systemTimeSupplier) {
        this.systemTimeSupplier = systemTimeSupplier;
        this.lastPoll = systemTimeSupplier.get();
    }

    @Override
    public void run() {
        if(++ticks < 40) {
            return;
        }
        ticks = 0;

        long now = systemTimeSupplier.get();
        long timeSpent = now - this.lastPoll;
        this.lastPoll = now;

        if (timeSpent <= 0) {
            return;
        }

        float tps = 1000 * 40 / (float)timeSpent;
        log(tps);
    }

    private void log(float tps) {
        tpsQueue.add(tps);
        if (tpsQueue.size() > TPS_QUEUE_SIZE) {
            tpsQueue.poll();
        }
    }

    public float getAverageTPS() {
        if (tpsQueue.isEmpty()) {
            return 20;
        }

        float sum = 0F;
        for (Float f : tpsQueue) {
            sum += f;
        }
        return sum / tpsQueue.size();
    }
}
