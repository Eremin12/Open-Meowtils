package wtf.tatp.meowtils.manager.slots;

import wtf.tatp.meowtils.manager.slots.impl.AutoChestSlot;
import wtf.tatp.meowtils.manager.slots.impl.ItemHighlightSlot;
import wtf.tatp.meowtils.manager.slots.impl.ShopHelperSlot;
import wtf.tatp.meowtils.manager.slots.impl.ShinyPotsSlot;

public class RegisterSlot {

    public static void init() {
        SlotManager.register(new ShopHelperSlot());
        SlotManager.register(new AutoChestSlot());
        SlotManager.register(new ShinyPotsSlot());
        SlotManager.register(new ItemHighlightSlot());
    }
}