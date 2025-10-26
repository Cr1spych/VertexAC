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

/**
 * Relationship
 */
public class AimD extends Check implements PacketCheck {
    public AimD(APlayer aPlayer) {
        super("AimD", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 3);
    }

    private double buffer;
    private double maxBuffer;
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitch = new ArrayList<>();
    private final int maxHistory = 20;

    // check config
    private final double maxCorrelation = 0.99;

    // check vars
    private double correlation;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || Math.abs(aPlayer.rotationData.deltaYaw) < 0.2 || Math.abs(aPlayer.rotationData.deltaPitch) < 0.2 || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat())
            return;

        if (PacketUtil.isRotation(event)) {
            deltaYaws.add((double) Math.abs(aPlayer.rotationData.deltaYaw));
            deltaPitch.add((double) Math.abs(aPlayer.rotationData.deltaPitch));

            if (deltaYaws.size() >= maxHistory) {
                correlation = Statistics.spearmanCorrelation(deltaYaws, deltaPitch);

                if (correlation > maxCorrelation) {
                    buffer++;
                    if (buffer > maxBuffer) {
                        flag("а чё корреляция такая высокая");
                        buffer = 0;
                    }
                } else {
                    if (buffer > 0) buffer--;
                }
                deltaYaws.clear();
                deltaPitch.clear();
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 3);
    }
}
