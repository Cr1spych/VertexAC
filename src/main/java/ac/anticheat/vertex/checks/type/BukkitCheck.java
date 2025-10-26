package ac.anticheat.vertex.checks.type;

import org.bukkit.event.Event;

public interface BukkitCheck {
    default void onEvent(Event event) {}
}
