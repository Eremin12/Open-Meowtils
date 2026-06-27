package wtf.tatp.meowtils.manager.session;

import java.util.function.BooleanSupplier;

public enum Bedwars {

    ALL(() -> SessionManager.bedwars),
    LOBBY(() -> SessionManager.bedwarsLobby),
    PRACTICE(() -> SessionManager.bedwarsPractice),
    PRE_GAME(() -> SessionManager.bedwarsGamePre),
    GAME(() -> SessionManager.bedwarsGame),
    SOLOS(() -> SessionManager.bedwarsGameSolos),
    DOUBLES(() -> SessionManager.bedwarsGameDoubles),
    THREES(() -> SessionManager.bedwarsGameThrees),
    FOURS(() -> SessionManager.bedwarsGameFours),
    ONE_BLOCK(() -> SessionManager.bedwarsGameOneBlock),
    FOUR_FOUR(() -> SessionManager.bedwarsGameFourFour);

    private final BooleanSupplier supplier;

    Bedwars(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public boolean isActive() {
        return this.supplier.getAsBoolean();
    }

    public boolean isNotActive() {
        return !isActive();
    }
}