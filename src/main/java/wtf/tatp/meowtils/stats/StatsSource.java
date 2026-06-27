package wtf.tatp.meowtils.stats;

import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.hypixel.Stats;

public interface StatsSource {

    String getId();

    StatsContainer fetch(String name);

    default long getCooldown() {
        Stats s = Module.get(Stats.class);
        return (s != null) ? s.cooldown : 100;
    }
}