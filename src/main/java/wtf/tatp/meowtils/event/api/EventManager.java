package wtf.tatp.meowtils.event.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public final class EventManager {

    private static final EnumMap<EventPriority, List<ListenerMethod>> LISTENERS = new EnumMap<>(EventPriority.class);

    static {
        for (EventPriority priority : EventPriority.values()) {
            LISTENERS.put(priority, new ArrayList<>());
        }
    }

    public static void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            EventTarget target = method.getAnnotation(EventTarget.class);

            if (target != null &&
                    method.getParameterCount() == 1 &&
                    Event.class.isAssignableFrom(method.getParameterTypes()[0])) {

                Class<? extends Event> eventType = (Class<? extends Event>) method.getParameterTypes()[0];
                List<ListenerMethod> listeners = LISTENERS.get(target.priority());

                boolean alreadyRegistered = listeners.stream().anyMatch(existing ->
                        existing.owner == listener && existing.method.equals(method) && existing.eventType == eventType);

                if (!alreadyRegistered) {
                    listeners.add(new ListenerMethod(listener, method, target.priority(), eventType));
                }
            }
        }
    }

    public static void unregister(Object listener) {
        for (List<ListenerMethod> list : LISTENERS.values()) {
            list.removeIf(lm -> lm.owner == listener);
        }
    }

    public static void post(Event event) {
        for (EventPriority priority : EventPriority.values()) {
            List<ListenerMethod> list = LISTENERS.get(priority);
            if (!list.isEmpty()) {
                ListenerMethod[] snapshot = list.toArray(new ListenerMethod[0]);

                for (ListenerMethod lm : snapshot) {
                    if (lm.eventType.isAssignableFrom(event.getClass())) {
                        lm.invoke(event);

                        if (event.isCancelled()) {
                            return;
                        }
                    }
                }
            }
        }
    }
}