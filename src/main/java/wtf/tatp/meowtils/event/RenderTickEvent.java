package wtf.tatp.meowtils.event;

import wtf.tatp.meowtils.event.api.Event;

public class RenderTickEvent extends Event {

    private final Phase phase;
    private final float partialTicks;

    public enum Phase {
        PRE,
        POST;
    }

    public RenderTickEvent(Phase phase, float partialTicks) {
        this.phase = phase;
        this.partialTicks = partialTicks;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}