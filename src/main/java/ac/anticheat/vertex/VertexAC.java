package ac.anticheat.vertex;

import ac.anticheat.vertex.api.events.VEventManager;
import ac.anticheat.vertex.commands.VertexCommand;
import ac.anticheat.vertex.listeners.*;
import ac.anticheat.vertex.managers.CheckManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class VertexAC extends JavaPlugin {
    private static VertexAC instance;
    private GatekeeperListener gatekeeperListener;
    private CheckManager checkManager;
    private VEventManager eventManager;

    @Override
    public void onEnable() {
        instance = this;
        gatekeeperListener = new GatekeeperListener();
        checkManager = new CheckManager();
        eventManager = new VEventManager();
        saveDefaultConfig();

        registerPacketListeners();

        getLogger().info("Vertex loaded");

        getServer().getOnlinePlayers().forEach(player -> {
            gatekeeperListener.getPlayers().put(player.getEntityId(), player);
            checkManager.registerChecks(player);
        });

        new VertexCommand(this);
        registerBukkitListeners();

        // метрики
        Metrics metrics = new Metrics(this, 27725);
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> checkManager.unregisterChecks(player));
    }

    private void registerPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketCheckListener(), PacketListenerPriority.NORMAL);

        PacketEvents.getAPI().getEventManager().registerListener(
                new InventoryListener(), PacketListenerPriority.NORMAL);
    }

    private void registerBukkitListeners() {
        getServer().getPluginManager().registerEvents(new TickListener(), this);
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

    public static VEventManager getEventManager() {
        return instance.eventManager;
    }
}
