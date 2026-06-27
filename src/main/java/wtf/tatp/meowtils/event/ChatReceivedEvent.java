package wtf.tatp.meowtils.event;

import net.minecraft.util.IChatComponent;
import wtf.tatp.meowtils.event.api.Event;

public class ChatReceivedEvent extends Event {

    public static final byte CHAT = 0;
    public static final byte SYSTEM = 1;
    public static final byte ACTION_BAR = 2;

    private final IChatComponent component;
    private final byte type;

    public ChatReceivedEvent(IChatComponent component, boolean isChat) {
        this.component = component;
        this.type = isChat ? CHAT : SYSTEM;
    }

    public IChatComponent getComponent() {
        return this.component;
    }

    public byte getType() {
        return this.type;
    }
}