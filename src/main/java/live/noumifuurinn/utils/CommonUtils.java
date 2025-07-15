package live.noumifuurinn.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 抽取出的与 mod 端有关的代码，方便不同 mod 端之间的移植
 */
@UtilityClass
@Slf4j
public class CommonUtils {
    private final Map<Object, Runnable> serverTickReg = new ConcurrentHashMap<>();

    static {
        // 注册服务器 tick 事件
        ServerTickEvents.START_SERVER_TICK.register(CommonUtils::onServerTick);
    }

    @Getter
    @Setter
    private static MinecraftServer server;

    public long[] getTickTimesNanos() {
        return server.getTickTimesNanos();
    }

    public void onPlayerJoin(Consumer<Player> consumer) {
        ServerPlayerEvents.JOIN.register(consumer::accept);
    }

    public void onPlayerLeave(Consumer<Player> consumer) {
        ServerPlayerEvents.LEAVE.register(consumer::accept);
    }

    public void executeAfter(long delay, TimeUnit timeUnit, Runnable task) {
        Thread.ofVirtual().start(() -> {
            delay(delay, timeUnit);
            task.run();
        });
    }

    public void registerServerTickEvent(Object parent, Runnable r) {
        serverTickReg.put(parent, r);
    }

    public void unregisterServerTickEvent(Object parent) {
        serverTickReg.remove(parent);
    }

    @SneakyThrows
    private void delay(long delay, TimeUnit timeUnit) {
        Thread.sleep(timeUnit.toMillis(delay));
    }

    private void onServerTick(MinecraftServer server) {
        for (Runnable r : serverTickReg.values()) {
            try {
                r.run();
            } catch (Throwable t) {
                log.warn("Error in server tick event", t);
            }
        }
    }
}
