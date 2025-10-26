package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.type.BukkitCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerEvent;

public class BukkitCheckListener implements Listener {

    public BukkitCheckListener() {
        VertexAC.getInstance().getServer().getPluginManager().registerEvent(
                Event.class,
                this,
                EventPriority.MONITOR,
                (listener, event) -> handleEvent(event),
                VertexAC.getInstance(),
                true
        );
    }

    private void handleEvent(Event e) {
        Player player = extractPlayer(e);
        if (player == null) return;

        for (BukkitCheck check : VertexAC.getCheckManager().getBukkitChecks(player)) {
            check.onEvent(e);
        }
    }

    private Player extractPlayer(Event event) {
        if (event instanceof PlayerEvent playerEvent) {
            return playerEvent.getPlayer();
        }
        return null;
    }
}