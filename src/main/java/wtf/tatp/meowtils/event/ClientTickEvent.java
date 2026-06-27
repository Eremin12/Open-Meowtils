package wtf.tatp.meowtils.event;

import wtf.tatp.meowtils.event.api.Event;

public class ClientTickEvent extends Event {

    private final Phase phase;

    public enum Phase {
        PRE,
        POST;
    }

    public ClientTickEvent(Phase phase) {
        this.phase = phase;
    }

    public Phase getPhase() {
        return this.phase;
    }
}