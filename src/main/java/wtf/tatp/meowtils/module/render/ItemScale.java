package wtf.tatp.meowtils.module.render;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public class ItemScale extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public float scale = 2.5F;
    @Config
    public boolean importantOnly = false;
    @Config
    public boolean resources = true;
    @Config
    public boolean gear = true;
    @Config
    public boolean heads = true;

    public ItemScale() {
        super("ItemScale", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Scale dropped items.");
        addSlider(new SliderValue("Scale", 0.5D, 5.0D, 0.1D, "x", "scale", this, float.class));
        addToggle(new ToggleValue("Important only", "importantOnly", this));
        addCheck(new CheckValue("§7Include §bGear", "gear", this));
        addCheck(new CheckValue("§7Include §6Resources", "resources", this));
        addCheck(new CheckValue("§7Include §dHeads", "heads", this));
    }

    public static boolean shouldScale(Item item) {
        ItemScale i = Module.get(ItemScale.class);
        if (i == null || item == null) return false;
        if (!i.importantOnly) return false;

        if (i.resources) {
            if (item == Items.iron_ingot) return true;
            if (item == Items.gold_ingot) return true;
            if (item == Items.diamond) return true;
            if (item == Items.emerald) return true;
            if (item == Items.golden_apple) return true;
            if (item == Items.potionitem) return true;
        }

        if (i.gear) {
            if (item instanceof net.minecraft.item.ItemSword) return true;
            if (item instanceof net.minecraft.item.ItemPickaxe) return true;
            if (item instanceof net.minecraft.item.ItemAxe) return true;
            if (item instanceof net.minecraft.item.ItemSpade) return true;
            if (item instanceof net.minecraft.item.ItemHoe) return true;
            if (item instanceof net.minecraft.item.ItemArmor) return true;
        }

        if (i.heads) {
            return (item == Items.skull);
        }

        return false;
    }
}