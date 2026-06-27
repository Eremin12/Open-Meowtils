package wtf.tatp.meowtils.module.advanced;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.GuiOpenEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.mixin.AccessorGuiContainer;
import wtf.tatp.meowtils.util.DelayedTask;

public class AutoChest extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int delay = 100;
    @Config
    public int waitDelay = 100;
    @Config
    public boolean renderClicked = true;
    @Config
    public boolean iron = true;
    @Config
    public boolean gold = true;
    @Config
    public boolean diamonds = true;
    @Config
    public boolean emeralds = true;

    private static final String LOCAL_CHEST = I18n.format("container.chest");
    private static final String LOCAL_ENDER_CHEST = I18n.format("container.enderchest");
    public static final Set<Integer> CLICKED_SLOTS = new HashSet<>();
    private long nextClickTime = 0L;
    private boolean shouldDeposit = false;
    private int lastCheckedSlot = 0;

    public AutoChest() {
        super("AutoChest", Module.Category.Advanced);
        tag(Module.ModuleTag.BLATANT);
        tooltip("Automatically deposit resources into a chest. Only works in bedwars.\n§cWARNING: §cLow §cdelays §care §cdetectable");
        addSlider(new SliderValue("Delay", 0.0D, 500.0D, 50.0D, "ms", "delay", this, int.class));
        addSlider(new SliderValue("Wait delay", 0.0D, 500.0D, 50.0D, "ms", "waitDelay", this, int.class));
        addToggle(new ToggleValue("Render clicked", "renderClicked", this));
        addCheck(new CheckValue("§7Iron Ingots", "iron", this));
        addCheck(new CheckValue("§6Gold Ingots", "gold", this));
        addCheck(new CheckValue("§bDiamonds", "diamonds", this));
        addCheck(new CheckValue("§2Emeralds", "emeralds", this));
    }

    @EventTarget
    public void onGuiOpen(GuiOpenEvent event) {
        if (Bedwars.ALL.isNotActive()) return;
        if (!(event.getGui() instanceof GuiChest)) {
            this.shouldDeposit = false;
            return;
        }

        int openDelay = this.waitDelay / 50;

        if (event.getGui() instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.getGui();
            if (chest.inventorySlots instanceof ContainerChest) {
                ContainerChest container = (ContainerChest) chest.inventorySlots;
                IInventory containerName = container.getLowerChestInventory();
                if (LOCAL_CHEST.equals(containerName.getName()) || LOCAL_ENDER_CHEST.equals(containerName.getName())) {
                    new DelayedTask(() -> {
                        this.shouldDeposit = true;
                        this.lastCheckedSlot = 0;
                    }, openDelay);
                }
            }
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.PRE) return;
        if (!(this.mc.currentScreen instanceof GuiChest)) return;
        if (Bedwars.ALL.isNotActive()) return;
        if (!this.shouldDeposit) return;

        if (this.renderClicked) {
            CLICKED_SLOTS.clear();
        }

        long now = System.currentTimeMillis();
        if (now >= this.nextClickTime) {
            this.nextClickTime = now + this.delay;
            boolean clicked = deposit((GuiContainer) this.mc.currentScreen);

            if (!clicked) {
                this.shouldDeposit = false;
            }
        }
    }

    private boolean deposit(GuiContainer gui) {
        for (int i = this.lastCheckedSlot; i < gui.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = gui.inventorySlots.getSlot(i);

            if (slot != null && slot.getHasStack() && slot.inventory == this.mc.thePlayer.inventory) {
                ItemStack stack = slot.getStack();
                if (stack != null) {
                    if (allowedItem(stack.getItem())) {
                        click(gui, slot, 0, 1);
                        this.lastCheckedSlot = i + 1;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void click(GuiContainer gui, Slot slot, int mouseButton, int type) {
        ((AccessorGuiContainer) gui).clickSlot(slot, slot.slotNumber, mouseButton, type);
        if (this.renderClicked) {
            CLICKED_SLOTS.add(slot.slotNumber);
        }
    }

    private boolean allowedItem(Item item) {
        return ((this.iron && item == Items.iron_ingot) || (this.gold && item == Items.gold_ingot) || (this.diamonds && item == Items.diamond) || (this.emeralds && item == Items.emerald));
    }

    @Override
    public void onDisable() {
        CLICKED_SLOTS.clear();
    }
}