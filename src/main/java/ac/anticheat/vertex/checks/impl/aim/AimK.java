package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.MathUtil;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimK extends Check implements PacketCheck {
    public AimK(APlayer aPlayer) {
        super("AimK", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;
    private final List<Double> deltaYaws = new ArrayList<>();
    private final int windowSize = 20;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            deltaYaws.add(deltaYaw);
            if (deltaYaws.size() > windowSize) {
                double[] diff = MathUtil.diff(deltaYaws);
                double diffSymmetry = MathUtil.symmetry(diff);
                if (diffSymmetry < 0.12) {
                    flag();
                }
                deltaYaws.clear();
            }
        }
    }
}
