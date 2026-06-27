package wtf.tatp.meowtils.util.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.antisnipe.AntiCheat;

public class AutoBlockCheck {
    //Its funny
    private int autoBlockTicks = 0;

    public void anticheatCheck(EntityPlayer player) {
        AntiCheat antiCheat = Module.get(AntiCheat.class);
        if (antiCheat == null || !antiCheat.autoBlock) {
            return;
        }

        if (player.isSwingInProgress && player.isBlocking()) {
            this.autoBlockTicks++;
        } else {
            this.autoBlockTicks = 0;
        }
    }

    public boolean failedAutoBlock() {
        return (this.autoBlockTicks > 10);
    }

    public void reset() {
        this.autoBlockTicks = 0;
    }
}