package live.noumifuurinn.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import live.noumifuurinn.utils.CommonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class PlayerOnline extends Metric {
    private final ConcurrentMap<UUID, PlayerStatus> status = new ConcurrentHashMap<>();

    public PlayerOnline(MeterRegistry registry) {
        super(registry);

        CommonUtils.onPlayerJoin(this::register);
        CommonUtils.onPlayerLeave(this::remove);
    }

    @Override
    public void disable() {
        if (!enabled) {
            return;
        }
        enabled = false;

        status.forEach((uuid, playerStatus) -> {
            if (playerStatus.gauge != null) {
                registry.remove(playerStatus.gauge);
            }
        });
        status.clear();
    }

    @Override
    public Collection<Meter> register() {
        for (ServerPlayer player : CommonUtils.getServer().getPlayerList().getPlayers()) {
            register(player);
        }
        return meters;
    }

    private void register(Player player) {
        status.computeIfAbsent(
                player.getUUID(),
                ignore -> {
                    PlayerStatus ps = new PlayerStatus();
                    ps.gauge = Gauge.builder("player.online", ps, PlayerStatus::getState)
                            .description("Online state by player name")
                            .tag("name", player.getName().getString())
                            .tag("uid", player.getUUID().toString())
                            .register(registry);
                    return ps;
                }
        ).state = 1;
    }

    private void remove(Player player) {
        UUID uuid = player.getUUID();
        PlayerStatus playerStatus = status.get(uuid);
        if (playerStatus == null || playerStatus.state == 0) {
            return;
        }

        playerStatus.state = 0;

        Gauge gauge = playerStatus.gauge;
        if (gauge == null) {
            return;
        }
        CommonUtils.executeAfter(5, TimeUnit.MINUTES, () -> {
            if (status.remove(uuid, new PlayerStatus(0))) {
                registry.remove(gauge);
            }
        });
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
