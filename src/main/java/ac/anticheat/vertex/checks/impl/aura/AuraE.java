package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AuraE extends Check implements PacketCheck {
    public AuraE(APlayer aPlayer) {
        super("AuraE", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }

    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        boolean invalid = aPlayer.actionData.attack() && !aPlayer.actionData.swing();

        if (PacketUtil.isAttack(event)) {
            if (invalid) {
                buffer++;
                if (buffer > maxBuffer) {
                    flag("атака без анимации");
                    buffer = 0;
                }
            } else {
                if (buffer > 0) buffer -= bufferDecrease;
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 2);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }
}
