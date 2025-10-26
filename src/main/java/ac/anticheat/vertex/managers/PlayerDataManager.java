package ac.anticheat.vertex.managers;

import ac.anticheat.vertex.player.APlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private static final Map<UUID, APlayer> players = new ConcurrentHashMap<>();

    public static APlayer get(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), uuid -> new APlayer(player));
    }

    public static void remove(Player player) {
        players.remove(player.getUniqueId());
    }
}
