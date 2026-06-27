package wtf.tatp.meowtils.manager.slots;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.Slot;

public class SlotManager {

    private static final List<SlotProvider> PROVIDERS = new ArrayList<>();

    public static void register(SlotProvider provider) {
        PROVIDERS.add(provider);
    }

    public static int getSlotColor(Slot slot) {
        for (SlotProvider provider : PROVIDERS) {
            if (provider.shouldRender(slot)) {
                return provider.getColor(slot);
            }
        }
        return 0;
    }
}