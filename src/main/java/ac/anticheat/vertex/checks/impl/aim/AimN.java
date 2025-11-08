package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimN extends Check implements PacketCheck {
    public AimN(APlayer aPlayer) {
        super("AimN", aPlayer);
    }

    private int ticks;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.bukkitPlayer.isInsideVehicle()) return;

        if (PacketUtil.isRotation(event)) {
            double pitch = Math.abs(aPlayer.rotationData.pitch);
            ticks++;

            if (pitch > 90 && ticks > 20) {
                flag();
                ticks = 0;
            }
        }
    }
}
