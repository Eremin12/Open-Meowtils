package wtf.tatp.meowtils.util.anticheat.checks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.antisnipe.AntiCheat;

public class NoSlowCheck {

    private int noSlowTicks = 0;
    private double lastPosX;
    private double lastPosZ;

    public void anticheatCheck(EntityPlayer player) {
        AntiCheat antiCheat = Module.get(AntiCheat.class);
        if (antiCheat == null || !antiCheat.noSlow) return;

        double deltaX = player.posX - this.lastPosX;
        double deltaZ = player.posZ - this.lastPosZ;
        double speed = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        if (player.isCollidedHorizontally && player.isUsingItem() && !player.isRiding()) {
            double baseThreshold = 0.05D;

            PotionEffect speedEffect = player.getActivePotionEffect(Potion.moveSpeed);
            if (speedEffect != null) {
                int amplifier = speedEffect.getAmplifier();
                baseThreshold *= 1.0D + 0.2D * (amplifier + 1);
            }

            if (speed > baseThreshold) {
                this.noSlowTicks++;
            } else {
                this.noSlowTicks = 0;
            }
        } else {
            this.noSlowTicks = 0;
        }
    }

    public boolean failedNoSlow() {
        return (this.noSlowTicks > 20);
    }

    public void reset() {
        this.noSlowTicks = 0;
    }
}