package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Simplification;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimL extends Check implements PacketCheck {
    public AimL(APlayer aPlayer) {
        super("AimL", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    private final List<Double> deltaYaws = new ArrayList<>();
    private double maxBuffer;
    private double bufferDecrease;
    private double buffer;
    private int infinitives;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            deltaYaws.add(deltaYaw);

            if (deltaYaws.size() > 10) {
                double deltaYawFirst = Math.abs(deltaYaws.get(0) - deltaYaws.get(1));
                double robotized = Math.abs(deltaYaw - deltaYawFirst);
                double interpolation = Simplification.scaleVal(deltaYaw / robotized, 2);

                if (Double.isInfinite(interpolation) && deltaYaw > 0) {
                    infinitives++;
                    if (infinitives > 1 && deltaYaw < 0.4) {
                        infinitives--;
                    }
                }

                if (infinitives > 1 && Math.abs(Statistics.getAverage(deltaYaws)) > 3.2) {
                    flag();
                    infinitives = 0;
                }
                deltaYaws.clear();
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }
}
