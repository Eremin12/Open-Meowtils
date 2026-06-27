package wtf.tatp.meowtils.module.advanced;

import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.AttackEntityEvent;
import wtf.tatp.meowtils.event.RenderTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public class AutoSwap extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean blocks = true;
    @Config
    public boolean projectiles = true;
    @Config
    public boolean resources = true;
    @Config
    public boolean pearls = true;
    @Config
    public boolean swords = true;
    @Config
    public boolean swordOnAttack = true;
    @Config
    public boolean tools = true;

    private static int lastSlot = -1;
    private static Item lastItem;

    public static final List<String> ALLOWED_BLOCKS = Arrays.asList("stone", "grass", "dirt", "planks", "wool", "wood", "glass", "leaves", "clay", "cloth", "log");
    private static final List<String> PROJECTILES = Arrays.asList("egg", "snowball");
    private static final List<String> PEARLS = Arrays.asList("pearl");
    private static final List<String> SWORDS = Arrays.asList("sword");
    private static final List<String> TOOLS = Arrays.asList("rod", "pickaxe", "axe", "shovel", "hoe", "flint_and_steel");
    private static final List<String> RESOURCES = Arrays.asList("265", "266", "388", "264");

    public AutoSwap() {
        super("AutoSwap", Module.Category.Advanced);
        tag(Module.ModuleTag.BLATANT);
        tooltip("Automatically swap slots when running out of certain items.");
        addToggle(new ToggleValue("§7Blocks", "blocks", this));
        addToggle(new ToggleValue("§6Projectiles", "projectiles", this));
        addToggle(new ToggleValue("§2Resources", "resources", this));
        addToggle(new ToggleValue("§5Pearls", "pearls", this));
        addToggle(new ToggleValue("§3Swords", "swords", this));
        addCheck(new CheckValue("Swap on attack", "swordOnAttack", this));
        addToggle(new ToggleValue("§eTools", "tools", this));
    }

    @EventTarget
    public void onRenderTick(RenderTickEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null || event.getPhase() != RenderTickEvent.Phase.POST) return;
        if (this.mc.currentScreen != null) return;

        int slot = this.mc.thePlayer.inventory.currentItem;
        ItemStack held = this.mc.thePlayer.inventory.getStackInSlot(slot);

        if (lastItem != null && slot == lastSlot && (held == null || held.stackSize < 1)) {
            swapItem(lastItem);
        }

        lastItem = (held != null) ? held.getItem() : null;
        lastSlot = slot;
    }

    @EventTarget
    public void onAttackEntity(AttackEntityEvent event) {
        if (!this.swordOnAttack) return;
        if (this.mc.thePlayer.getHeldItem() == null || !(this.mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword)) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemSword) {
                    this.mc.thePlayer.inventory.currentItem = i;
                }
            }
        }
    }

    private void swapItem(Item lastItem) {
        if (lastItem == null) return;

        int itemId = Item.getIdFromItem(lastItem);
        String idString = String.valueOf(itemId);
        String lastId = lastItem.getUnlocalizedName().toLowerCase();

        boolean isBlock = lastItem instanceof net.minecraft.item.ItemBlock;
        int current = this.mc.thePlayer.inventory.currentItem;
        List<String> category = null;

        if (!isBlock)
            if (PROJECTILES.stream().anyMatch(lastId::contains) && !lastId.contains("leggings") && this.projectiles) { category = PROJECTILES; }
            else if (PEARLS.stream().anyMatch(lastId::contains) && this.pearls) { category = PEARLS; }
            else if (SWORDS.stream().anyMatch(lastId::contains) && this.swords) { category = SWORDS; }
            else if (TOOLS.stream().anyMatch(lastId::contains) && this.tools) { category = TOOLS; }
            else if (RESOURCES.stream().anyMatch(idString::contains) && this.resources) { category = RESOURCES; }
            else { return; }

        for (int offset = 1; offset <= 9; offset++) {
            int i = (current + offset) % 9;
            ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.stackSize >= 1) {
                Item item = stack.getItem();
                String id = item.getUnlocalizedName().toLowerCase();
                String numericId = String.valueOf(Item.getIdFromItem(item));

                if (item == lastItem) {
                    this.mc.thePlayer.inventory.currentItem = i;
                    return;
                }

                if (isBlock && this.blocks && isValidBlock(stack)) {
                    this.mc.thePlayer.inventory.currentItem = i;
                    return;
                }

                if (category != null && (category.stream().anyMatch(id::contains) || category.stream().anyMatch(numericId::contains)) && !id.contains("leggings")) {
                    this.mc.thePlayer.inventory.currentItem = i;
                    return;
                }
            }
        }
    }

    public static boolean isValidBlock(ItemStack stack) {
        AutoSwap autoSwap = Module.get(AutoSwap.class);
        if (autoSwap == null || !autoSwap.blocks) return false;
        if (!(stack.getItem() instanceof net.minecraft.item.ItemBlock)) return false;
        String id = stack.getItem().getUnlocalizedName().toLowerCase();
        return ALLOWED_BLOCKS.stream().anyMatch(id::contains);
    }
}