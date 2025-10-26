package ac.anticheat.vertex.managers;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.impl.aim.*;
import ac.anticheat.vertex.checks.impl.aura.*;
import ac.anticheat.vertex.player.APlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class CheckManager {
    private final Map<UUID, List<Check>> packetChecks = new HashMap<>();

    public void registerChecks(Player player) {
        APlayer aPlayer = new APlayer(player);
        List<Check> playerChecks = new ArrayList<>();

        playerChecks.add(aPlayer.actionData);
        playerChecks.add(aPlayer.rotationData);
        playerChecks.add(aPlayer.packetData);

        playerChecks.add(new AimA(aPlayer));
        playerChecks.add(new AimB(aPlayer));
        playerChecks.add(new AimC(aPlayer));
        playerChecks.add(new AimD(aPlayer));
        playerChecks.add(new AimE(aPlayer));
        playerChecks.add(new AuraA(aPlayer));
        playerChecks.add(new AuraB(aPlayer));
        playerChecks.add(new AuraC(aPlayer));
        playerChecks.add(new AuraD(aPlayer));

        for (Check check : playerChecks) {
            check.resetViolations();
        }

        packetChecks.put(player.getUniqueId(), playerChecks);
    }

    public void unregisterChecks(Player player) {
        List<Check> playerChecks = packetChecks.remove(player.getUniqueId());
        if (playerChecks != null) {
            for (Check check : playerChecks) {
                check.resetViolations();
                check.cancelDecayTask();
            }
        }
    }

    public List<Check> getChecks(Player player) {
        return packetChecks.getOrDefault(player.getUniqueId(), Collections.emptyList());
    }

    public Check getCheck(Player player, String name) {
        List<Check> playerChecks = packetChecks.get(player.getUniqueId());
        if (playerChecks == null) return null;

        for (Check check : playerChecks) {
            if (check.getName().equalsIgnoreCase(name)) {
                return check;
            }
        }
        return null;
    }
}