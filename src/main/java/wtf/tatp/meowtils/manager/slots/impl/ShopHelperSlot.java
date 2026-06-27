package wtf.tatp.meowtils.manager.slots.impl;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.manager.slots.SlotProvider;
import wtf.tatp.meowtils.module.bedwars.ShopHelper;

public class ShopHelperSlot implements SlotProvider {

    @Override
    public boolean shouldRender(Slot slot) {
        ShopHelper q = Module.get(ShopHelper.class);
        if (q == null) return false;
        return (q.enabled && q.highlightAffordable && (Bedwars.GAME.isActive() || Duels.BEDWARS.isActive()));
    }

    @Override
    public int getColor(Slot slot) {
        if (slot == null || !slot.getHasStack()) return 0;

        ItemStack stack = slot.getStack();
        ShopHelper.ItemCost cost = ShopHelper.getCostFromLore(stack);

        if (cost == null) return 0;
        if (!ShopHelper.shouldHighlight(stack, cost)) return 0;
        return ShopHelper.getColor(cost.resourceType);
    }
}