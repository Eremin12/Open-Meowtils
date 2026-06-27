package wtf.tatp.meowtils.manager.session;

import java.util.function.BooleanSupplier;

public enum Server {

    HYPIXEL(() -> SessionManager.hypixel),
    HYPIXEL_REPLAY(() -> SessionManager.hypixelReplay),
    UNIVERSAL(() -> SessionManager.universalServer),
    MINEPLEX(() -> SessionManager.mineplex);

    private final BooleanSupplier supplier;

    Server(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public boolean isActive() {
        return this.supplier.getAsBoolean();
    }

    public boolean isNotActive() {
        return !isActive();
    }
}