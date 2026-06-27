package wtf.tatp.meowtils.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiContainer.class)
public interface AccessorGuiContainer {

  @Invoker("handleMouseClick")
  void clickSlot(Slot slot, int slotId, int clickedButton, int clickType);
}