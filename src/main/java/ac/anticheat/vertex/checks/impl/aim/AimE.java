package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * ну типа a > 3.5 а b == 0
 */
public class AimE extends Check implements PacketCheck {
    private double maxBuffer;

    public AimE(APlayer aPlayer) {
        super("AimE", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 7);
    }

    private double bufferYaw;
    private double bufferPitch;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || Math.abs(aPlayer.rotationData.pitch) == 90 || !aPlayer.actionData.inCombat()) return;

        float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
        float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

        if (PacketUtil.isRotation(event)) {
            if (deltaYaw > 3.5 && deltaPitch == 0) {
                bufferYaw++;
                if (bufferYaw > maxBuffer) {
                    flag("ну типа х > 3.5 а y == 0");
                    bufferYaw = 0;
                }
            } else {
                if (bufferYaw > 0) bufferYaw -= 0.1;
            }

            if (deltaPitch > 3.5 && deltaYaw == 0) {
                bufferPitch++;
                if (bufferPitch > maxBuffer) {
                    flag("ну типа y > 3.5 а х == 0");
                    bufferPitch = 0;
                }
            } else {
                if (bufferPitch > 0) bufferPitch -= 0.1;
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 7);
    }
}
