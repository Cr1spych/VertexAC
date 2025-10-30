package ac.anticheat.vertex.commands;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.managers.CheckManager;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.Hex;
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
            sender.sendMessage(Hex.translateHexColors(Config.getString("messages.no-permission", "§cYou don't have permission to use this command")));
            return;
        }

        if (sender instanceof Player) {
            Logger.log((Player) sender, Hex.translateHexColors(Config.getString("messages.config.config-reload", "§aReloading config...")));
        } else {
            Logger.log(Hex.translateHexColors(Config.getString("messages.config.config-reload", "§aReloading config...")));
        }
        plugin.reloadConfig();

        Bukkit.getOnlinePlayers().forEach(player -> {
            for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                if (check instanceof PacketCheck packetCheck) {
                    packetCheck.onReload();
                }
            }
        });

        if (sender instanceof Player) {
            Logger.log((Player) sender, Hex.translateHexColors(Config.getString("messages.config.config-reloaded", "§aConfig reloaded")));
        } else {
            Logger.log(Hex.translateHexColors(Config.getString("messages.config.config-reloaded", "§aConfig reloaded")));
        }
    }

    private void checks(CommandSender sender) {
        if (!sender.hasPermission("vertex.checks")) {
            sender.sendMessage(Hex.translateHexColors(Config.getString("messages.no-permission", "§cYou don't have permission to use this command")));
            return;
        }

        if (sender instanceof Player) {
            Logger.log((Player) sender, Hex.translateHexColors(Config.getString("messages.checks.header", "§aEnabled checks:")));
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                    if (check.isEnabled()) {
                        if (check.getName().toLowerCase().contains("data")) continue;
                        sender.sendMessage(Hex.translateHexColors(Config.getString("messages.checks.check", "§a- &f{check}")).replace("{check}", check.getName()));
                    }
                }
                break;
            }
        } else {
            Logger.log(Hex.translateHexColors(Config.getString("messages.checks.header", "§aEnabled checks:")));
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Check check : VertexAC.getCheckManager().getChecks(player)) {
                    if (check.isEnabled()) {
                        if (check.getName().toLowerCase().contains("data")) continue;
                        sender.sendMessage(Hex.translateHexColors(Config.getString("messages.checks.check", "§a- &f{check}")).replace("{check}", check.getName()));
                    }
                }
                break;
            }
        }
    }

    private void toggleAlerts(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players");
            return;
        }

        if (!sender.hasPermission("vertex.alerts")) {
            sender.sendMessage(Hex.translateHexColors(Config.getString("messages.no-permission", "§cYou don't have permission to use this command")));
            return;
        }

        APlayer aPlayer = PlayerDataManager.get((Player) sender);

        aPlayer.toggleAlerts();
        if (aPlayer.sendAlerts()) {
            Logger.log((Player) sender, Hex.translateHexColors(Config.getString("messages.alerts.alerts-enabled", "§aAlerts enabled")));
        } else {
            Logger.log((Player) sender, Hex.translateHexColors(Config.getString("messages.alerts.alerts-disabled", "§cAlerts disabled")));
        }
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("vertex.help")) {
            sender.sendMessage(Hex.translateHexColors(Config.getString("messages.no-permission", "§cYou don't have permission to use this command")));
            return;
        }

        if (sender instanceof Player) {
            Logger.log((Player) sender, Hex.translateHexColors(Config.getString("messages.commands", "messages.commands")));
        } else {
            Logger.log(Hex.translateHexColors(Config.getString("messages.commands", "messages.commands")));
        }
    }
}