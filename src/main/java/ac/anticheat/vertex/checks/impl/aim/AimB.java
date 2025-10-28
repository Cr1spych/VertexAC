package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * округление
 */
public class AimB extends Check implements PacketCheck {
    public AimB(APlayer aPlayer) {
        super("AimB", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 5);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.1);
    }

    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation())
            return;

        if (PacketUtil.isRotation(event)) {
            float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            boolean yawRounded = Math.round(deltaYaw) - deltaYaw == 0 && deltaYaw != 0;
            boolean pitchRounded = Math.round(deltaPitch) - deltaPitch == 0 && deltaPitch != 0;

            if (yawRounded || pitchRounded) {
                buffer++;
                if (buffer > maxBuffer) {
                    flag("а чё он этава округляет");
                    buffer = 0;
                }
            } else {
                if (buffer > 0) buffer -= bufferDecrease;
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 5);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.1);
    }
}