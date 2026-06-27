package wtf.tatp.meowtils.event;

import wtf.tatp.meowtils.event.api.Event;

public class RenderStringEvent extends Event {

    private String string;

    public RenderStringEvent(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    public void setString(String string) {
        this.string = string;
    }
}