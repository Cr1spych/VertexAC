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

public class AimO extends Check implements PacketCheck {
    public AimO(APlayer aPlayer) {
        super("AimO", aPlayer);
        this.maxBuffer = Config.getDouble("checks.AimO.max-buffer", 2);
        this.bufferDecrease = Config.getDouble("checks.AimO.buffer-decrease", 0.25);
        this.distinctThreshold = Config.getDouble("checks.AimO.distinct-threshold", 50);
    }

    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private final int maxHistorySize = 100;
    private double distinctThreshold;
    private double buffer1;
    private double buffer2;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            double deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            if (deltaYaw > 0.35 && deltaYaw < 20) {
                deltaYaws.add(deltaYaw);
            }
            if (deltaPitch > 0.35 && deltaPitch < 20) {
                deltaPitches.add(deltaPitch);
            }

            if (deltaYaws.size() > maxHistorySize) {
                double distinct = Statistics.getDistinct(deltaYaws);

                if (distinct < distinctThreshold) {
                    buffer1++;
                    if (buffer1 > maxBuffer) {
                        flag();
                        buffer1 = 0;
                    }
                } else {
                    if (buffer1 > 0) buffer1 -= bufferDecrease;
                }

                deltaYaws.remove(0);
            }
            if (deltaPitches.size() > maxHistorySize) {
                double distinct = Statistics.getDistinct(deltaPitches);

                if (distinct < distinctThreshold) {
                    buffer2++;
                    if (buffer2 > maxBuffer) {
                        flag();
                        buffer2 = 0;
                    }
                } else {
                    if (buffer2 > 0) buffer2 -= bufferDecrease;
                }

                deltaPitches.remove(0);
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
        this.distinctThreshold = Config.getDouble(getConfigPath() + ".distinct-threshold", 50);
    }
}
