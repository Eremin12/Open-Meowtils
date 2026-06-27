package wtf.tatp.meowtils.mixin;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.utility.HotbarLock;
import wtf.tatp.meowtils.util.Render;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    private void meowtils$renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player, CallbackInfo ci) {
        if (player == null) return;

        ItemStack stack = player.inventory.getStackInSlot(index);
        HotbarLock h = Module.get(HotbarLock.class);

        if (h == null || !h.enabled) return;
        if (!h.showLocked) return;

        if (h.mode.equals("Manual")) {
            if (HotbarLock.isSlotLocked(index + 1)) {
                Render.drawSlotBackground(xPos, yPos, HotbarLock.LOCK_COLOR);
            }
        } else if (h.mode.equals("Swords") && stack != null && stack.getItem() instanceof ItemSword) {
            Render.drawSlotBackground(xPos, yPos, HotbarLock.LOCK_COLOR);
        }
    }
}