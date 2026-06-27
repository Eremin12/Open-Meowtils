package wtf.tatp.meowtils.event;

import wtf.tatp.meowtils.event.api.Event;

public class MouseInputEvent extends Event {

    private final Action action;

    public enum Action {
        RIGHT_CLICK,
        LEFT_CLICK,
        MIDDLE_CLICK;
    }

    public MouseInputEvent(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return this.action;
    }
}