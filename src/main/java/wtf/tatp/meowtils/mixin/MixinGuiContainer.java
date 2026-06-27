package wtf.tatp.meowtils.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.event.SlotClickEvent;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.manager.slots.SlotManager;
import wtf.tatp.meowtils.util.Render;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void meowtils$drawSlot(Slot slot, CallbackInfo ci) {
        if (slot == null) return;

        int color = SlotManager.getSlotColor(slot);
        if (color != 0) {
            Render.drawSlotBackground(slot.xDisplayPosition, slot.yDisplayPosition, color);
        }
    }

    @Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
    private void meowtils$handleMouseClick(Slot slot, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (slot == null) return;

        GuiContainer gui = (GuiContainer) (Object) this;

        SlotClickEvent event = new SlotClickEvent(gui, slot, slotId, clickedButton, clickType);
        EventManager.post(event);

        if (event.getClickedButton() != clickedButton || event.getClickType() != clickType) {
            gui.mc.playerController.windowClick(gui.inventorySlots.windowId, slotId, event.getClickedButton(), event.getClickType(), gui.mc.thePlayer);
            ci.cancel();
            return;
        }

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (event.getReplaceClick()) {
            gui.mc.playerController.windowClick(gui.inventorySlots.windowId, slotId, 2, 3, gui.mc.thePlayer);
            ci.cancel();
        }
    }
}