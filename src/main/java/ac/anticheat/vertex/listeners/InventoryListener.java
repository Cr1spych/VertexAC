package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

public class InventoryListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPlayer() == null) return;

        APlayer aPlayer = PlayerDataManager.get(event.getPlayer());

        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            int windowId = wrapper.getWindowId();

            aPlayer.hasInventoryOpened = true;
            aPlayer.windowId = windowId;
        }

        if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            aPlayer.hasInventoryOpened = false;
        }
    }
}
