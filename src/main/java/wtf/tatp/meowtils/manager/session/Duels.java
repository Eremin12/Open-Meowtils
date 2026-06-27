package wtf.tatp.meowtils.manager.session;

import java.util.function.BooleanSupplier;

public enum Duels {

    ALL(() -> SessionManager.duels),
    LOBBY(() -> SessionManager.duelsLobby),
    BEDWARS(() -> SessionManager.duelsBedwars);

    private final BooleanSupplier supplier;

    Duels(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public boolean isActive() {
        return this.supplier.getAsBoolean();
    }

    public boolean isNotActive() {
        return !isActive();
    }
}