package wtf.tatp.meowtils.module.advanced;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.util.DelayedTask;

public class AutoStairs extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;

    private static double lastY = 0.0D;

    public AutoStairs() {
        super("AutoStairs", Module.Category.Advanced);
        tag(Module.ModuleTag.BLATANT);
        tooltip("Automatically jumps when going up stairs, for faster movement.");
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        double currentY = this.mc.thePlayer.posY;

        if (this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedHorizontally && !this.mc.thePlayer.isPotionActive(Potion.jump)) {
            double heightGain = currentY - lastY;
            BlockPos below = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 0.1D, this.mc.thePlayer.posZ);

            if (heightGain == 0.5D && this.mc.theWorld.getBlockState(below).getBlock() instanceof net.minecraft.block.BlockStairs) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), true);
                Meowtils.debugMessage(EnumChatFormatting.YELLOW + "[AutoStairs]: " + EnumChatFormatting.RESET + "Jumped!");
                new DelayedTask(() -> KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), false), 1);
            }
        }
        lastY = currentY;
    }
}