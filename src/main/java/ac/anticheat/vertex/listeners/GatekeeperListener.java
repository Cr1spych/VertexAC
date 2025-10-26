package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.managers.CheckManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GatekeeperListener implements Listener {
    private final Map<Integer, Player> players = new ConcurrentHashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        players.put(player.getEntityId(), player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        players.remove(player.getEntityId());
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public Player getPlayerById(int id) {
        return players.get(id);
    }
}
