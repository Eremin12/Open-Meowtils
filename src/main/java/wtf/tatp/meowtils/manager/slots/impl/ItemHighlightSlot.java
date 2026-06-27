package wtf.tatp.meowtils.manager.slots.impl;

import net.minecraft.inventory.Slot;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.manager.slots.SlotProvider;
import wtf.tatp.meowtils.module.skywars.ItemHighlight;

public class ItemHighlightSlot implements SlotProvider {

    @Override
    public boolean shouldRender(Slot slot) {
        ItemHighlight itemHighlight = Module.get(ItemHighlight.class);
        if (itemHighlight == null || !itemHighlight.enabled) return false;
        if (!Skywars.GAME.isActive() && !Skywars.MINI.isActive()) return false;
        return (slot.getStack() != null && slot.getStack().getItem() != null);
    }

    @Override
    public int getColor(Slot slot) {
        ItemHighlight h = Module.get(ItemHighlight.class);
        if (h == null || slot.getStack() == null) return 0;

        if (h.showBlacklisted && ItemHighlight.isBlacklisted(ItemHighlight.getListName(slot.getStack()))) {
            return ItemHighlight.getColor(h.blacklistColor);
        }

        if (h.showBest && ItemHighlight.shouldHighlight(slot.getStack())) {
            return ItemHighlight.getColor(h.bestColor);
        }

        if (h.showSafelisted && ItemHighlight.isSafelisted(ItemHighlight.getListName(slot.getStack()))) {
            return ItemHighlight.getColor(h.safelistColor);
        }

        return 0;
    }
}