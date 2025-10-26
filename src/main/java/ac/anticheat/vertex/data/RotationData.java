package ac.anticheat.vertex.data;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;

import java.util.ArrayList;
import java.util.List;

public class RotationData extends Check implements PacketCheck {

    public float yaw, pitch;
    public float lastYaw, lastPitch;
    public float deltaYaw, deltaPitch;
    public float lastDeltaYaw, lastDeltaPitch;
    public float lastLastDeltaYaw, lastLastDeltaPitch; // Для вычисления ускорений второго порядка

    private long lastSmooth = 0L, lastHighRate = 0L;
    private double lastDeltaXRot = 0.0, lastDeltaYRot = 0.0;
    private final List<Double> yawSamples = new ArrayList<>();
    private final List<Double> pitchSamples = new ArrayList<>();
    private boolean cinematicRotation = false;

    public RotationData(APlayer aPlayer) {
        super("RotationData", aPlayer);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtil.isRotation(event)) {
            WrapperPlayClientPlayerRotation wrapper = new WrapperPlayClientPlayerRotation(event);
            updateRotation(wrapper.getYaw(), wrapper.getPitch());
        }
    }

    private void updateRotation(float newYaw, float newPitch) {
        lastYaw = yaw;
        lastPitch = pitch;
        yaw = newYaw;
        pitch = newPitch;

        float newDeltaYaw = yaw - lastYaw;
        float newDeltaPitch = pitch - lastPitch;

        lastLastDeltaYaw = lastDeltaYaw;
        lastLastDeltaPitch = lastDeltaPitch;

        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;

        deltaYaw = newDeltaYaw;
        deltaPitch = newDeltaPitch;

        processCinematic();
    }

    private void processCinematic() {
        long now = System.currentTimeMillis();

        double differenceYaw = Math.abs(deltaYaw - lastDeltaXRot);
        double differencePitch = Math.abs(deltaPitch - lastDeltaYRot);

        double joltYaw = Math.abs(differenceYaw - deltaYaw);
        double joltPitch = Math.abs(differencePitch - deltaPitch);

        boolean cinematic = (now - lastHighRate > 250L) || (now - lastSmooth < 9000L);

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            lastHighRate = now;
        }

        if (deltaYaw > 0.0 && deltaPitch > 0.0) {
            yawSamples.add((double) deltaYaw);
            pitchSamples.add((double) deltaPitch);
        }

        if (yawSamples.size() >= 20 && pitchSamples.size() >= 20) {
            int negativesYaw = countNegatives(yawSamples);
            int negativesPitch = countNegatives(pitchSamples);

            int positivesYaw = countPositives(yawSamples);
            int positivesPitch = countPositives(pitchSamples);

            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                lastSmooth = now;
            }

            yawSamples.clear();
            pitchSamples.clear();
        }

        lastDeltaXRot = deltaYaw;
        lastDeltaYRot = deltaPitch;
        cinematicRotation = cinematic;
    }

    private int countNegatives(List<Double> values) {
        int negatives = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) - values.get(i - 1) < 0) negatives++;
        }
        return negatives;
    }

    private int countPositives(List<Double> values) {
        int positives = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) - values.get(i - 1) > 0) positives++;
        }
        return positives;
    }

    public boolean isCinematicRotation() {
        return cinematicRotation;
    }
}
