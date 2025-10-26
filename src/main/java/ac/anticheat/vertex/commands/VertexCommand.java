package ac.anticheat.vertex.commands;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.managers.CheckManager;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Logger;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VertexCommand implements CommandExecutor {

    private final VertexAC plugin;

    public VertexCommand(VertexAC plugin) {
        this.plugin = plugin;
        plugin.getCommand("vertex").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reload(sender);
            case "checks" -> checks(sender);
            case "help" -> sendHelp(sender);
            case "alerts" -> toggleAlerts(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("vertex.reload")) {
            sender.sendMessage("§cYou don't have permission to use this command");
            return;
        }

        Logger.log((Player) sender, "§aReloading config...");
        plugin.reloadConfig();

        Bukkit.getOnlinePlayers().forEach(player -> {
            for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                if (check instanceof PacketCheck packetCheck) {
                    packetCheck.onReload();
                }
            }
        });

        Logger.log((Player) sender, "§aConfig reloaded");
    }

    private void checks(CommandSender sender) {
        if (!sender.hasPermission("vertex.checks")) {
            sender.sendMessage("§cYou don't have permission to use this command");
            return;
        }

        Logger.log((Player) sender, "§aEnabled checks:");
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                if (check.isEnabled()) {
                    if (check.getName().toLowerCase().contains("data")) continue;
                    sender.sendMessage(" §a- §f" + check.getName());
                }
            }
            break;
        }
    }

    private void toggleAlerts(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players");
            return;
        }

        if (!sender.hasPermission("vertex.alerts")) {
            sender.sendMessage("§cYou don't have permission to use this command");
            return;
        }

        APlayer aPlayer = PlayerDataManager.get((Player) sender);

        aPlayer.toggleAlerts();
        if (aPlayer.sendAlerts()) {
            Logger.log((Player) sender, "§aAlerts enabled");
        } else {
            Logger.log((Player) sender, "§cAlerts disabled");
        }
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("vertex.help")) {
            sender.sendMessage("§cYou don't have permission to use this command");
            return;
        }

        sender.sendMessage("""
                §aCommands:
                 §a- §f/vertex reload §7- config reload
                 §a- §f/vertex checks §7- enabled checks
                 §a- §f/vertex help §7- help
                 §a- §f/vertex alerts §7- toggle alerts
                """);
    }
}