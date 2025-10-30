package ac.anticheat.vertex.api.events;

import java.util.ArrayList;
import java.util.List;

public class VEventManager {
    private List<VEventListener> listeners = new ArrayList<>();

    public void registerListener(VEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(VEventListener listener) {
        listeners.remove(listener);
    }

    public void call(VEvent VEvent) {
        for (VEventListener listener : listeners) {
            listener.onEvent(VEvent);
        }
    }
}
