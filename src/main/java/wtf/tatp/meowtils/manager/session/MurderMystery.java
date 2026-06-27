package wtf.tatp.meowtils.manager.session;

import java.util.function.BooleanSupplier;

public enum MurderMystery {

    ALL(() -> SessionManager.murderMystery);

    private final BooleanSupplier supplier;

    MurderMystery(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public boolean isActive() {
        return this.supplier.getAsBoolean();
    }

    public boolean isNotActive() {
        return !isActive();
    }
}