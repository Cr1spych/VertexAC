package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.managers.CheckManager;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Logger;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CheckListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        APlayer aPlayer = PlayerDataManager.get(e.getPlayer());
        VertexAC.getCheckManager().registerChecks(e.getPlayer());
        if (aPlayer.toggleAlertsOnJoin() && !aPlayer.sendAlerts() && e.getPlayer().hasPermission("swagger.alerts")) {
            aPlayer.setSendAlerts(true);
            Logger.log(aPlayer.bukkitPlayer, "§aAlerts enabled");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        VertexAC.getCheckManager().unregisterChecks(e.getPlayer());
        PlayerDataManager.remove(e.getPlayer());
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                if (check.hitTicksToCancel > 0) {
                    check.hitTicksToCancel--;
                }
            }
        }
    }
}
