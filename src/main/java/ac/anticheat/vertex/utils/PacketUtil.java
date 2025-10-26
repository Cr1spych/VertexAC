package ac.anticheat.vertex.utils;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

public class PacketUtil {

    public static boolean isRotation(PacketReceiveEvent event) {
        return event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION;
    }

    public static boolean isMovement(PacketReceiveEvent event) {
        return event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION;
    }

    public static boolean isAttack(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            return wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK;
        }
        return false;
    }

    public static boolean isStartFlyingWithElytra(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
            return wrapper.getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA;
        }
        return false;
    }
}
