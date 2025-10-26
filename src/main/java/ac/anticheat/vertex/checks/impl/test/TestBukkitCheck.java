package ac.anticheat.vertex.checks.impl.test;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.BukkitCheck;
import ac.anticheat.vertex.player.APlayer;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.event.Event;

public class TestBukkitCheck extends Check implements BukkitCheck {
    public TestBukkitCheck(APlayer aPlayer) {
        super("TestBukkitCheck", aPlayer);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof PlayerJumpEvent) {
            aPlayer.bukkitPlayer.sendMessage("Ого ты прыгнул");
        }
    }
}
