package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.MathUtil;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;
/**
 * idk
 */
public class AimC extends Check implements PacketCheck {
    public AimC(APlayer aPlayer) {
        super("AimC", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 3);
    }

    private double buffer;
    private double maxBuffer;
    private final List<Double> deltaYaw = new ArrayList<>();
    private final int maxHistory = 20;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || Math.abs(aPlayer.rotationData.deltaYaw) < 0.2 || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat())
            return;

        if (PacketUtil.isRotation(event)) {
            deltaYaw.add((double) Math.abs(aPlayer.rotationData.deltaYaw));
            if (deltaYaw.size() >= maxHistory) {
                deltaYaw.clear();
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 3);
    }
}
