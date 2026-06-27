package wtf.tatp.meowtils.module.utility;

import java.util.Arrays;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.KeyInputEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.ColorUtil;
import net.minecraft.item.ItemSword;

public class HotbarLock extends Module {

    @Config
    public boolean enabled = false;

    @Config
    public int key = 0;

    @Config
    public String mode = "Manual";

    @Config
    public boolean showLocked = true;

    @Config
    public boolean slot1 = true;

    @Config
    public boolean slot2 = true;

    @Config
    public boolean slot3 = true;

    @Config
    public boolean slot4 = true;

    @Config
    public boolean slot5 = true;

    @Config
    public boolean slot6 = true;

    @Config
    public boolean slot7 = true;

    @Config
    public boolean slot8 = true;

    @Config
    public boolean slot9 = true;

    public static final int LOCK_COLOR = ColorUtil.rgba(
            ColorUtil.getColorFromFormatting(EnumChatFormatting.RED).getRed(),
            ColorUtil.getColorFromFormatting(EnumChatFormatting.RED).getGreen(),
            ColorUtil.getColorFromFormatting(EnumChatFormatting.RED).getBlue(), 160);

    public HotbarLock() {
        super("HotbarLock", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Lock slots in your hotbar.");
        addMode(new ModeValue("Mode", Arrays.asList("Manual", "Swords"), "mode", this));
        addToggle(new ToggleValue("Show locked slots", "showLocked", this));
        addExpand(new ExpandValue("Slots", e -> {
            e.addCheck(new CheckValue("Slot 1", "slot1", this));
            e.addCheck(new CheckValue("Slot 2", "slot2", this));
            e.addCheck(new CheckValue("Slot 3", "slot3", this));
            e.addCheck(new CheckValue("Slot 4", "slot4", this));
            e.addCheck(new CheckValue("Slot 5", "slot5", this));
            e.addCheck(new CheckValue("Slot 6", "slot6", this));
            e.addCheck(new CheckValue("Slot 7", "slot7", this));
            e.addCheck(new CheckValue("Slot 8", "slot8", this));
            e.addCheck(new CheckValue("Slot 9", "slot9", this));
        }));
    }

//    @EventTarget
//    public void onKeyInput(KeyInputEvent event) {
//        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
//        if (this.mc.currentScreen != null) return;
//
//        if (event.getKey() == this.mc.gameSettings.keyBindDrop.getKeyCode()) {
//            boolean cancel = false;
//
//            if (this.mode.equals("Manual") && isSlotLocked(this.mc.thePlayer.inventory.currentItem + 1)) {
//                cancel = true;
//                Meowtils.addMessage(EnumChatFormatting.RED + "Prevented you from dropping item in locked slot!");
//            }
//
//            if (this.mode.equals("Swords") && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
//                cancel = true;
//                Meowtils.addMessage(EnumChatFormatting.RED + "Prevented you from dropping your sword!");
//            }
//
//            if (cancel) {
//                event.setCancelled(true);
//            }
//        }
//    }
    public static boolean isSlotLocked(int slot) {
        HotbarLock s = Module.get(HotbarLock.class);
        if (s == null) return false;

        switch (slot) {
            case 1: return s.slot1;
            case 2: return s.slot2;
            case 3: return s.slot3;
            case 4: return s.slot4;
            case 5: return s.slot5;
            case 6: return s.slot6;
            case 7: return s.slot7;
            case 8: return s.slot8;
            case 9: return s.slot9;
        }

        return false;
    }
}