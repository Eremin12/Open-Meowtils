package wtf.tatp.meowtils.event.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;

public final class ListenerMethod {

    private static long lastAlert = -1L;

    public final Object owner;
    public final Method method;
    public final EventPriority priority;
    public final Class<? extends Event> eventType;

    public ListenerMethod(Object owner, Method method, EventPriority priority, Class<? extends Event> eventType) {
        this.owner = owner;
        this.method = method;
        this.priority = priority;
        this.eventType = eventType;
        this.method.setAccessible(true);
    }

    public void invoke(Event event) {
        try {
            this.method.invoke(this.owner, event);
        } catch (Throwable t) {
            Throwable cause = (t instanceof InvocationTargetException && t.getCause() != null) ? t.getCause() : t;
            long now = System.currentTimeMillis();

            if (now - lastAlert > 1000L) {
                Meowtils.addMessage(EnumChatFormatting.RED + "There was an error in: " + this.owner.getClass().getSimpleName());
                lastAlert = now;
            }

            Meowtils.fatal("There was an event error in: " + this.owner.getClass().getName());
            cause.printStackTrace();
        }
    }
}