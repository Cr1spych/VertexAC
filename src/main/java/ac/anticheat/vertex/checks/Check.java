package ac.anticheat.vertex.checks;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.beauty.PunishEffect;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.Hex;
import ac.anticheat.vertex.utils.MessageUtils;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;

public abstract class Check {
    private final String name;
    protected final APlayer aPlayer;
    private final boolean enabled;
    private final boolean setback;
    private final String punishCommand;
    private final boolean alert;
    private int violations;
    private final int maxViolations;
    public int hitCancelTicks;
    public int hitTicksToCancel;
    private Plugin plugin;

    private BukkitTask decayTask;
    private Location lastLocation;

    public Check(String name, APlayer aPlayer) {
        this.name = name;
        this.aPlayer = aPlayer;
        this.enabled = Config.getBoolean("checks." + name + ".enabled", true);
        this.setback = false;
        this.punishCommand = Config.getString("checks." + name + ".punish-command", "");
        this.alert = Config.getBoolean("checks." + name + ".alert", true);
        this.maxViolations = Config.getInt("checks." + name + ".max-violations", 10);
        this.hitCancelTicks = Config.getInt("checks." + name + ".hit-cancel-ticks", 100);
        this.hitTicksToCancel = 0;
        this.plugin = VertexAC.getInstance();

        this.lastLocation = aPlayer.bukkitPlayer.getLocation().clone();

        startDecayTask();
    }

    public Check(String name, APlayer aPlayer, boolean setback) {
        this.name = name;
        this.aPlayer = aPlayer;
        this.enabled = Config.getBoolean("checks." + name + ".enabled", true);
        this.setback = setback;
        this.punishCommand = Config.getString("checks." + name + ".punish-command", "");
        this.alert = Config.getBoolean("checks." + name + ".alert", true);
        this.maxViolations = Config.getInt("checks." + name + ".max-violations", 10);

        this.lastLocation = aPlayer.bukkitPlayer.getLocation().clone();

        startDecayTask();
    }

    protected void flag() {
        flag("");
    }

    protected void flag(String verbose) {
        this.hitTicksToCancel += hitCancelTicks;

        if (violations < maxViolations) {
            violations++;
            aPlayer.globalVl++;
            aPlayer.kaNpcVl++;
        }

        String rawMessage = Config.getString("alerts.message", "");

        Component message = MessageUtils.parseMessage(
                rawMessage
                        .replace("{prefix}", Config.getString("vertex.prefix", ""))
                        .replace("{player}", aPlayer.bukkitPlayer.getName())
                        .replace("{check}", name)
                        .replace("{violations}", String.valueOf(violations))
        ).hoverEvent(
                HoverEvent.showText(
                        MessageUtils.parseMessage(verbose)
                )
        );
        if (alert && violations <= maxViolations && aPlayer.bukkitPlayer.isOnline()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("vertex.alerts")) {
                    APlayer targetData = PlayerDataManager.get(online);
                    if (targetData != null && targetData.sendAlerts()) {
                        online.sendMessage(message);
                    }
                }
            }
        }

        if (setback && aPlayer.bukkitPlayer.isOnline()) {
            setback();
        }

        if (getViolations() >= getMaxViolations()) {
            runSync(() -> PunishEffect.start(aPlayer.bukkitPlayer));
            dispatchCommand(Hex.translateHexColors(punishCommand));
        }
    }

    protected void setback() {
        if (lastLocation != null && aPlayer.bukkitPlayer.isOnline()) {
            runSync(() -> aPlayer.bukkitPlayer.teleport(lastLocation));
        }
    }

    protected void updateLastLocation(Location loc) {
        if (loc != null) {
            this.lastLocation = loc.clone();
        }
    }

    private void dispatchCommand(String command) {
        String finalCommand = Hex.translateHexColors(command);

        runSync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand.replace("{player}", aPlayer.bukkitPlayer.getName()));
            resetViolations();
        });
    }

    protected void cancel(PacketReceiveEvent event) {
        event.setCancelled(true);
    }

    private void startDecayTask() {
        long rawDelay = Config.getInt(getConfigPath() + ".remove-violations-after", 300);
        long delay = rawDelay * 20;

        this.decayTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (violations > 0) {
                    int decay = Config.getInt(getConfigPath() + ".decay", 1);
                    int oldViolations = violations;

                    violations -= decay;
                    if (violations < 0) violations = 0;
                }
            }
        }.runTaskTimer(VertexAC.getInstance(), delay, delay);
    }

    public void runSync(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runLater(Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    public void cancelDecayTask() {
        if (decayTask != null) {
            decayTask.cancel();
        }
    }

    public void updateLastLocation() {
        if (aPlayer != null && aPlayer.bukkitPlayer.isOnline()) {
            this.lastLocation = aPlayer.bukkitPlayer.getLocation().clone();
        }
    }

    public void resetViolations() {
        this.violations = 0;
        aPlayer.globalVl = 0;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getConfigPath() {
        return "checks." + name;
    }

    public boolean alert() {
        return alert;
    }

    public int getViolations() {
        return violations;
    }

    public int getMaxViolations() {
        return maxViolations;
    }
}