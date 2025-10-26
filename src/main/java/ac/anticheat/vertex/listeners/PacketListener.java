package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.managers.CheckManager;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import org.bukkit.entity.Player;

public class PacketListener implements com.github.retrooper.packetevents.event.PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        if (PacketUtil.isMovement(event)) {
            for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                check.updateLastLocation();
            }
        }

        for (Check check : VertexAC.getCheckManager().getChecks(player)) {
            if (check instanceof PacketCheck packetCheck) {
                packetCheck.onPacketReceive(event);
            }
        }

        if (PacketUtil.isAttack(event)) {
            for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                if (check.hitTicksToCancel > 0) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        for (Check check : VertexAC.getCheckManager().getChecks(player)) {
            if (check instanceof PacketCheck packetCheck) {
                packetCheck.onPacketSend(event);
            }
        }
    }
}
