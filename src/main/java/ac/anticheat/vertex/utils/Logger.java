package ac.anticheat.vertex.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Logger {
    private static final String prefix = Config.getString("vertex.prefix", "vertex.prefix");

    public static void log(String message) {
        Bukkit.getLogger().info(Hex.translateHexColors(prefix + "§7 " + message));
    }

    public static void log(Player player, String message) {
        player.sendMessage(Hex.translateHexColors(prefix + "§7 " + message));
    }
}
