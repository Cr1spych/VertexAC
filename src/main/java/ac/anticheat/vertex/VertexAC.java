package ac.anticheat.vertex;

import ac.anticheat.vertex.commands.VertexCommand;
import ac.anticheat.vertex.listeners.CheckListener;
import ac.anticheat.vertex.listeners.GatekeeperListener;
import ac.anticheat.vertex.listeners.InventoryListener;
import ac.anticheat.vertex.listeners.PacketListener;
import ac.anticheat.vertex.managers.CheckManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class VertexAC extends JavaPlugin {
    private static VertexAC instance;
    private GatekeeperListener gatekeeperListener;
    private CheckManager checkManager;

    @Override
    public void onEnable() {
        instance = this;
        gatekeeperListener = new GatekeeperListener();
        checkManager = new CheckManager();
        saveDefaultConfig();

        registerPacketListeners();

        getLogger().info("Vertex loaded");

        getServer().getOnlinePlayers().forEach(player -> {
            gatekeeperListener.getPlayers().put(player.getEntityId(), player);
            checkManager.registerChecks(player);
        });

        new VertexCommand(this);
        registerBukkitListeners();
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> checkManager.unregisterChecks(player));
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketListener(), PacketListenerPriority.NORMAL);

        PacketEvents.getAPI().getEventManager().registerListener(
                new InventoryListener(), PacketListenerPriority.NORMAL);
    }

    private void registerBukkitListeners() {
        getServer().getPluginManager().registerEvents(new CheckListener(), this);
        getServer().getPluginManager().registerEvents(gatekeeperListener, this);
    }

    public static VertexAC getInstance() {
        return instance;
    }

    public static GatekeeperListener getGatekeeperListener() {
        return instance.gatekeeperListener;
    }

    public static CheckManager getCheckManager() {
        return instance.checkManager;
    }
}
