package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * Snap
 */
public class AuraD extends Check implements PacketCheck {
    public AuraD(APlayer aPlayer) {
        super("AuraD", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 1);
    }

    private int buffer;
    private double maxBuffer;

    // check vars
    private double deltaYaw;
    private double lastDeltaYaw;

    // check config
    private final double max = 60;
    private final double min = 2.5;

    private int vls;
    private int a;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtil.isAttack(event)) {
            deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            lastDeltaYaw = Math.abs(aPlayer.rotationData.lastDeltaYaw);

            if (deltaYaw > max && lastDeltaYaw < min) {
                buffer++;
                if (buffer > maxBuffer) {
                    flag("снапы задетектило");
                    buffer = 0;
                    vls++;
                }
            } else {
                if (buffer > 0) buffer--;
            }

            if (deltaYaw > 45 && vls > 0) {
                flag("снапы задетектило");
                vls = 0;
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
    }
}
