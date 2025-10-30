package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * удары через стену
 */
public class AuraB extends Check implements PacketCheck {
    public AuraB(APlayer player) {
        super("AuraB", player);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 3);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }

    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                Player target = aPlayer.actionData.getPTarget();
                if (target == null) return;

                Bukkit.getScheduler().runTask(VertexAC.getInstance(), () -> {
                    if (wallHit$$$elfcode(aPlayer.bukkitPlayer, target)) {
                        buffer++;
                        if (buffer > maxBuffer) {
                            flag("удары через стену");
                            buffer = 0;
                        }
                    } else {
                        if (buffer > 0) buffer -= bufferDecrease;
                    }
                });
            }
        }
    }

    private boolean wallHit$$$elfcode(Player from, Player target) {
        Location origin = from.getLocation().add(0, from.getEyeHeight(), 0);

        double width = target.getWidth() / 2.0;
        double minX = target.getLocation().getX() - width;
        double maxX = target.getLocation().getX() + width;
        double minY = target.getLocation().getY();
        double maxY = target.getLocation().getY() + target.getHeight();
        double minZ = target.getLocation().getZ() - width;
        double maxZ = target.getLocation().getZ() + width;

        List<Vector> points = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            double x = minX + i * (maxX - minX) / 2.0;
            for (int j = 0; j <= 2; j++) {
                double y = minY + j * (maxY - minY) / 2.0;
                for (int k = 0; k <= 2; k++) {
                    double z = minZ + k * (maxZ - minZ) / 2.0;
                    points.add(new Vector(x, y, z));
                }
            }
        }

        for (Vector point : points) {
            Vector direction = point.clone().subtract(origin.toVector());
            RayTraceResult result = from.getWorld().rayTraceBlocks(
                    origin,
                    direction.normalize(),
                    direction.length(),
                    FluidCollisionMode.NEVER,
                    true
            );

            if (result == null || result.getHitPosition() == null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 3);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }
}
