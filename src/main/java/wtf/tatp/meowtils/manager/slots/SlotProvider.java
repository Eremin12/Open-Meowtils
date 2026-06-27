package wtf.tatp.meowtils.manager.slots;

import net.minecraft.inventory.Slot;

public interface SlotProvider {

  boolean shouldRender(Slot slot);

  int getColor(Slot slot);
}