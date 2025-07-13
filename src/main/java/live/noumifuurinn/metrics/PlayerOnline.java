package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.FabricExporter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerOnline extends Metric {
    private final ConcurrentMap<UUID, PlayerStatus> status = new ConcurrentHashMap<>();

    public PlayerOnline(MeterRegistry registry) {
        super(registry);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            register(handler.player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            remove(handler.player);
        });

    }

    @Override
    public Set<Meter> register() {
        var meters = new HashSet<Meter>();
        for (ServerPlayer player : FabricExporter.getServer().getPlayerList().getPlayers()) {
            meters.add(register(player));
        }
        return meters;
    }

    private Meter register(ServerPlayer player) {
        PlayerStatus playerStatus = status.computeIfAbsent(
                player.getUUID(),
                ignore -> {
                    PlayerStatus ps = new PlayerStatus();
                    ps.gauge = Gauge.builder(prefix("player.online"), ps, PlayerStatus::getState)
                            .description("Online state by player name")
                            .tag("name", player.getName().getString())
                            .tag("uid", player.getUUID().toString())
                            .register(registry);
                    return ps;
                }
        );
        playerStatus.state = 1;
        return playerStatus.gauge;
    }

    private void remove(ServerPlayer player) {
        UUID uuid = player.getUUID();
        PlayerStatus playerStatus = status.get(uuid);
        if (playerStatus == null || playerStatus.state == 0) {
            return;
        }

        playerStatus.state = 0;
        Thread.ofVirtual().start(() -> remove(uuid, playerStatus));
    }

    @SneakyThrows
    private void remove(UUID uuid, PlayerStatus playerStatus) {
        Gauge gauge = playerStatus.gauge;
        if (gauge == null) {
            return;
        }

        // 等待5分钟后删除指标
        Thread.sleep(5 * 60_000);
        if (status.remove(uuid, new PlayerStatus(0))) {
            registry.remove(gauge);
            meters.remove(gauge);
        }
    }

    @Data
    @NoArgsConstructor
    private static class PlayerStatus {
        private double state;
        @EqualsAndHashCode.Exclude
        private Gauge gauge;

        public PlayerStatus(double state) {
            this.state = state;
        }
    }
}
