package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimG extends Check implements PacketCheck {
    public AimG(APlayer aPlayer) {
        super("AimG*", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
        this.zeroCRThreshold = Config.getDouble(getConfigPath() + ".zcr-threshold", 0.5);
        this.avgDeltaYawThreshold = Config.getDouble(getConfigPath() + ".avg-delta-yaw-threshold", 3);
    }

    private final List<Double> deltaYaws = new ArrayList<>();
    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;
    private double zeroCRThreshold;
    private double avgDeltaYawThreshold;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = aPlayer.rotationData.deltaYaw;
            deltaYaws.add(deltaYaw);
            if (deltaYaws.size() > 20) {
                double zeroCR = Statistics.getZCR(deltaYaws);
                double avgDeltaYaw = Statistics.getAverage(deltaYaws);

                if (zeroCR > zeroCRThreshold && Math.abs(avgDeltaYaw) > avgDeltaYawThreshold) {
                    buffer++;
                    if (buffer > maxBuffer) {
                        flag();
                        buffer = 0;
                    }
                } else {
                    if (buffer > 0) buffer -= bufferDecrease;
                }

                deltaYaws.clear();
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
        this.zeroCRThreshold = Config.getDouble(getConfigPath() + ".zcr-threshold", 0.5);
        this.avgDeltaYawThreshold = Config.getDouble(getConfigPath() + ".avg-delta-yaw-threshold", 3);
    }
}
