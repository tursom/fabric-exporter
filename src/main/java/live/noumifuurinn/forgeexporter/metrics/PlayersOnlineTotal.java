package live.noumifuurinn.forgeexporter.metrics;

import io.prometheus.client.Gauge;
import net.minecraft.server.level.ServerLevel;

public class PlayersOnlineTotal extends WorldMetric {

    private static final Gauge PLAYERS_ONLINE = Gauge.build()
            .name(prefix("players_online_total"))
            .help("Players currently online per world")
            .labelNames("world", "mod")
            .create();

    public PlayersOnlineTotal() {
        super(PLAYERS_ONLINE);
    }

    @Override
    protected void clear() {
    }

    @Override
    protected void collect(ServerLevel world) {
        String name = world.dimension().location().getPath();
        String mod = world.dimension().location().getNamespace();
        PLAYERS_ONLINE.labels(name, mod).set(world.getPlayers(p -> true).size());
    }
}
