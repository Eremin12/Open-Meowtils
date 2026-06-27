package wtf.tatp.meowtils.manager.slots.impl;

import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.manager.slots.SlotProvider;
import wtf.tatp.meowtils.module.advanced.AutoChest;
import wtf.tatp.meowtils.util.ColorUtil;

public class AutoChestSlot implements SlotProvider {

    @Override
    public boolean shouldRender(Slot slot) {
        AutoChest a = Module.get(AutoChest.class);
        if (a == null) return false;
        return (a.enabled && a.renderClicked && AutoChest.CLICKED_SLOTS.contains(slot.slotNumber));
    }

    @Override
    public int getColor(Slot slot) {
        return ColorUtil.getRGBFromFormatting(EnumChatFormatting.LIGHT_PURPLE);
    }
}