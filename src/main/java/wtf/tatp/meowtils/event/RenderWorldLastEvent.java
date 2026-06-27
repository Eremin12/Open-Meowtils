package wtf.tatp.meowtils.event;

import wtf.tatp.meowtils.event.api.Event;

public class RenderWorldLastEvent extends Event {

    private final float partialTicks;

    public RenderWorldLastEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}