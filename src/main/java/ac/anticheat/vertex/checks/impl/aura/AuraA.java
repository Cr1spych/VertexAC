package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * Invalid attacks
 */
public class AuraA extends Check implements PacketCheck {
    public AuraA(APlayer player) {
        super("AuraA", player);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
    }

    private double maxBuffer;
    private double buffer;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled()) return;

        if (PacketUtil.isAttack(event)) {
            if (aPlayer.bukkitPlayer.isHandRaised()) {
                buffer++;
                if (buffer > maxBuffer) {
                    flag("не ну а чё он жрёт и бьёт");
                    buffer = 0;
                }
            } else {
                if (buffer > 0) buffer--;
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
    }
}
