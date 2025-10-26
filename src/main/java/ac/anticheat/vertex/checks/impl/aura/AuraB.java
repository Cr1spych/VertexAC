package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

/**
 * хз
 */
public class AuraB extends Check implements PacketCheck {
    public AuraB(APlayer player) {
        super("AuraB", player);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 0);
    }

    private int buffer;
    private double maxBuffer;

//    @Override
//    public void onPacketReceive(PacketReceiveEvent event) {
//        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
//            WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
//            if (wrapper.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
//                if (aPlayer.bukkitPlayer.isSprinting()) {
//                    buffer++;
//                    if (buffer > maxBuffer) {
//                        flag("сброс спринта инвалид писал");
//                    } else {
//                        if (buffer > 0) buffer--;
//                    }
//                }
//            }
//        }
//    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 0);
    }
}
