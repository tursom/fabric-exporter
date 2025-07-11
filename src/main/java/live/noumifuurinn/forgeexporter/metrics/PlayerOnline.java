package live.noumifuurinn.forgeexporter.metrics;

import live.noumifuurinn.forgeexporter.FabricExporter;
import io.prometheus.client.Gauge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerOnline extends Metric {
    public static class PlayerCacheItem {
        private String name;
        private String uuid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    private static final Gauge PLAYERS_WITH_NAMES = Gauge.build()
            .name(prefix("player_online"))
            .help("Online state by player name")
            .labelNames("name", "uid")
            .create();
    private static final Map<UUID, String> userMap = new ConcurrentHashMap<>();
    boolean init = false;

    public PlayerOnline() {
        super(PLAYERS_WITH_NAMES);
    }

    @Override
    protected void doCollect() {
        if(!init) {
            init = true;
            for (var player : FabricExporter.getServer().getProfileCache().profilesByUUID.values()) {
                userMap.put(player.getProfile().getId(), player.getProfile().getName());
            }
        }

        PLAYERS_WITH_NAMES.clear();
        PlayerList list = FabricExporter.getServer().getPlayerList();
        List<ServerPlayer> newPlayers = list.getPlayers().stream().filter(p -> !userMap.containsKey(p.getUUID())).toList();

        List<Map.Entry<UUID, String>> players = userMap.entrySet().stream().toList();
        for(Map.Entry<UUID, String> entry : players) {
            ServerPlayer player = list.getPlayer(entry.getKey());
            if(player == null) {
                PLAYERS_WITH_NAMES.labels(entry.getValue(), entry.getKey().toString()).set(0);
            } else {
                String name = player.getGameProfile().getName();
                PLAYERS_WITH_NAMES.labels(name, entry.getKey().toString()).set(1);
                userMap.put(entry.getKey(), name);
            }
        }

        for(ServerPlayer player : newPlayers) {
            String name = player.getGameProfile().getName();

            PLAYERS_WITH_NAMES.labels(name, player.getUUID().toString()).set(1);
            userMap.put(player.getUUID(), name);
        }
    }


}
