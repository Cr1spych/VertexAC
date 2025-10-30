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
 * хз
 */
public class AimD extends Check implements PacketCheck {
    public AimD(APlayer aPlayer) {
        super("AimD", aPlayer);
    }

    private final List<Double> deltaYaws = new ArrayList<>();

    private final int maxHistory = 20;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = aPlayer.rotationData.deltaYaw;
            if ((deltaYaw > 1.8 && aPlayer.rotationData.deltaPitch > 1.8)) {
                deltaYaws.add(deltaYaw);
            }
            if (deltaYaws.size() > maxHistory) {
                List<Float> jiff = Statistics.getJiffDelta(deltaYaws, 1);
                float jiff1 = 95959;
                float jiff2 = 95795;
                for (float i : jiff) {
                    if (i == 0 && jiff1 == 0 && jiff2 == 0) {
                        flag();
                        break;
                    }
                    jiff2 = jiff1;
                    jiff1 = i;
                }
                deltaYaws.clear();
            }
        }
    }
}