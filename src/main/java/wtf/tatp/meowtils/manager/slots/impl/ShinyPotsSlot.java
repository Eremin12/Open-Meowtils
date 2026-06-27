package wtf.tatp.meowtils.manager.slots.impl;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.slots.SlotProvider;
import wtf.tatp.meowtils.module.render.ShinyPots;

public class ShinyPotsSlot implements SlotProvider {

    @Override
    public boolean shouldRender(Slot slot) {
        ShinyPots s = Module.get(ShinyPots.class);
        if (s == null || !s.enabled) return false;
        if (!slot.getHasStack()) return false;
        return slot.getStack().getItem() instanceof ItemPotion;
    }

    @Override
    public int getColor(Slot slot) {
        return slot.getStack().getItem().getColorFromItemStack(slot.getStack(), 0) | 0xCC000000;
    }
}