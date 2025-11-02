package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.HashMap;
import java.util.Map;

public class AimH extends Check implements PacketCheck {

    public AimH(APlayer aPlayer) {
        super("AimH", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    private static final int windowSize = 60;
    private static final int minValid = 20;
    private static final float minDelta = 0.005f;
    private static final float maxDelta = 40.0f;
    private static final float q = 0.05f;
    private static final double domRatio = 0.82;
    private static final double yawSumNeed = 10.0;
    private final int[] qBuf = new int[windowSize];
    private final boolean[] valid = new boolean[windowSize];
    private int idx = 0, count = 0, validCount = 0;
    private final Map<Integer, Integer> hist = new HashMap<>();
    private double yawSum = 0.0;
    private int stableStreak = 0;

    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (!PacketUtil.isRotation(event)) return;

        float deltaYaw = aPlayer.rotationData.deltaYaw;
        float ady = Math.abs(deltaYaw);
        boolean isValid = ady >= minDelta && ady <= maxDelta;

        if (count == windowSize) {
            if (valid[idx]) {
                int oldQ = qBuf[idx];
                yawSum -= Math.abs(oldQ * q);
                validCount--;
                int c = hist.getOrDefault(oldQ, 0) - 1;
                if (c <= 0) hist.remove(oldQ); else hist.put(oldQ, c);
            }
        } else {
            count++;
        }

        if (isValid) {
            int q = Math.max(1, Math.round(ady / AimH.q));
            qBuf[idx] = q;
            valid[idx] = true;
            validCount++;
            yawSum += q * AimH.q;
            hist.put(q, hist.getOrDefault(q, 0) + 1);
        } else {
            qBuf[idx] = 0;
            valid[idx] = false;
        }

        idx = (idx + 1) % windowSize;

        if (validCount >= minValid) {
            int top1 = 0, top2 = 0;
            for (int c : hist.values()) {
                if (c > top1) { top2 = top1; top1 = c; }
                else if (c > top2) { top2 = c; }
            }

            int dom = top1 + top2;
            double ratio = (validCount > 0) ? ((double) dom / validCount) : 0.0;

            if (ratio >= domRatio && yawSum >= yawSumNeed) {
                if (++stableStreak >= 2) {
                    buffer++;
                    if (buffer > maxBuffer) {
                        flag();
                        buffer = 0;
                    }
                    stableStreak = 1;
                } else {
                    buffer = Math.max(0, buffer - bufferDecrease);
                }
            } else {
                stableStreak = Math.max(0, stableStreak - 1);
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.05);
    }
}