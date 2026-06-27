package wtf.tatp.meowtils.event;

import net.minecraft.client.settings.KeyBinding;
import wtf.tatp.meowtils.event.api.Event;

public class KeyInputEvent extends Event {

    private final KeyBinding key;

    public KeyInputEvent(KeyBinding key) {
        this.key = key;
    }

    public KeyBinding getKey() {
        return this.key;
    }
}