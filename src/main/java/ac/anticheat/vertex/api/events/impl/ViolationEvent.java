package ac.anticheat.vertex.api.events.impl;

import ac.anticheat.vertex.api.events.VEvent;
import ac.anticheat.vertex.checks.Check;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class ViolationEvent extends VEvent {
    private final Player player;
    private final Check check;
    private final double vl;
}
