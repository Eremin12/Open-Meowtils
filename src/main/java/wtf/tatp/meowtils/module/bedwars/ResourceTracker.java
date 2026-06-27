package wtf.tatp.meowtils.module.bedwars;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventPriority;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.util.Prefix;
import wtf.tatp.meowtils.util.Render;
import wtf.tatp.meowtils.util.Util;

public class ResourceTracker extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean stackMessages = true;
    @Config
    public String soundMode = "Important";
    @Config
    public boolean hideDefault = true;
    @Config
    public boolean iron = true;
    @Config
    public boolean gold = true;
    @Config
    public boolean diamond = true;
    @Config
    public boolean emerald = true;
    @Config
    public boolean enderchest = true;
    @Config
    public String mode = "Both";
    @Config
    public float scale = 0.65F;
    @Config
    public int posX = 1;
    @Config
    public int posY = 1;

    private static final Item[] TRACKED_ITEMS = new Item[] { Items.iron_ingot, Items.gold_ingot, Items.diamond, Items.emerald };
    private static final Map<Item, Integer> INVENTORY_RESOURCES = new HashMap<>();
    private static final Map<Item, Integer> CHEST_RESOURCES = new HashMap<>();
    private static final Map<Item, Integer> CURRENT_RESOURCES = new HashMap<>();

    public ResourceTracker() {
        super("ResourceTracker", Module.Category.Bedwars);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Tracks resources in your inventory.");
        addMode(new ModeValue("Display", Arrays.asList("Both", "HUD", "Chat"), "mode", this));
        addMode(new ModeValue("Ping sound", Arrays.asList("All", "Important", "None"), "soundMode", this));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addToggle(new ToggleValue("Stack messages", "stackMessages", this));
        addToggle(new ToggleValue("Hide default message", "hideDefault", this));
        addCheck(new CheckValue("Include §5Enderchest", "enderchest", this));
        addCheck(new CheckValue("Track §7Iron Ingots", "iron", this));
        addCheck(new CheckValue("Track §6Gold Ingots", "gold", this));
        addCheck(new CheckValue("Track §bDiamonds", "diamond", this));
        addCheck(new CheckValue("Track §2Emeralds", "emerald", this));
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (this.mc.currentScreen != null && !GuiUtil.inEditor()) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive() && !GuiUtil.inEditor()) return;
        if (Bedwars.PRE_GAME.isActive() && !GuiUtil.inEditor()) return;
        if (this.mode.equals("Chat")) return;

        int x = this.posX;
        int y = this.posY;

        for (Item item : TRACKED_ITEMS) {
            if (shouldTrack(item)) {
                int inventoryCount = INVENTORY_RESOURCES.getOrDefault(item, 0);
                int chestCount = CHEST_RESOURCES.getOrDefault(item, 0);

                Render.renderItemIcon(new ItemStack(item), x, y, this.scale);

                String text = String.valueOf(inventoryCount);
                if (this.enderchest) {
                    text = text + EnumChatFormatting.DARK_GRAY + " + " + getItemColor(item) + chestCount;
                }

                Meowtils.drawString(getItemColor(item) + text, (int) (x + 22.0F * this.scale), (int) (y + 4.0F * this.scale), this.scale, -1);
                y += (int) (Meowtils.offsetString(this.scale) + 6.0F * this.scale);
            }
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;
        if (Bedwars.PRE_GAME.isActive()) return;

        CURRENT_RESOURCES.clear();

        for (ItemStack stack : this.mc.thePlayer.inventory.mainInventory) {
            if (stack != null && shouldTrack(stack.getItem())) {
                CURRENT_RESOURCES.put(stack.getItem(), CURRENT_RESOURCES.getOrDefault(stack.getItem(), 0) + stack.stackSize);
            }
        }

        checkResourceChange();

        INVENTORY_RESOURCES.clear();
        INVENTORY_RESOURCES.putAll(CURRENT_RESOURCES);

        checkEnderChest();
    }

    @EventTarget(priority = EventPriority.LOWEST)
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();
        if (Bedwars.GAME.isNotActive() || Bedwars.PRE_GAME.isActive()) return;
        if (!this.hideDefault) return;

        if (msg.startsWith("+") && (msg.contains("Iron") || msg.contains("Gold") || msg.contains("Emerald") || msg.contains("Diamond"))) {
            event.setCancelled(true);
        }
    }

    private void checkResourceChange() {
        for (Item item : TRACKED_ITEMS) {
            if (shouldTrack(item)) {
                int newCount = CURRENT_RESOURCES.getOrDefault(item, 0);
                int oldCount = INVENTORY_RESOURCES.getOrDefault(item, 0);

                if (!this.mode.equals("HUD") && newCount != oldCount) {
                    String itemName = getItemName(item);
                    boolean gained = (newCount > oldCount);

                    String prefix = gained ? (EnumChatFormatting.GREEN + "[+] ") : (EnumChatFormatting.RED + "[-] ");

                    if (this.stackMessages) {
                        sendStackedMessage(Prefix.getPrefix() + prefix + itemName + " " + EnumChatFormatting.DARK_GRAY + "(" + newCount + ")", setChatId(item));
                    } else {
                        Meowtils.addMessage(prefix + itemName + " " + EnumChatFormatting.DARK_GRAY + "(" + newCount + ")");
                    }

                    if (this.soundMode.equals("All") || (this.soundMode.equals("Important") && (itemName.contains("Diamond") || itemName.contains("Emerald")))) {
                        if (gained) {
                            Util.playSound(Util.Sound.LEVEL, 100);
                        } else {
                            Util.playSound(Util.Sound.ERROR, 100);
                        }
                    }
                }
            }
        }
    }

    private void checkEnderChest() {
        Container container = this.mc.thePlayer.openContainer;
        if (container instanceof ContainerChest) {
            IInventory lowerChest = ((ContainerChest) container).getLowerChestInventory();
            String localizedEnderChest = I18n.format("container.enderchest");

            if (lowerChest.getName().equals(localizedEnderChest)) {
                Map<Item, Integer> tempChestMap = new HashMap<>();

                for (int i = 0; i < lowerChest.getSizeInventory(); i++) {
                    ItemStack stack = lowerChest.getStackInSlot(i);
                    if (stack != null && shouldTrack(stack.getItem())) {
                        tempChestMap.put(stack.getItem(), tempChestMap.getOrDefault(stack.getItem(), 0) + stack.stackSize);
                    }
                }
                CHEST_RESOURCES.clear();
                CHEST_RESOURCES.putAll(tempChestMap);
            }
        }
    }

    private String getItemName(Item item) {
        if (item == Items.iron_ingot) return EnumChatFormatting.WHITE + "Iron";
        if (item == Items.gold_ingot) return EnumChatFormatting.GOLD + "Gold";
        if (item == Items.diamond) return EnumChatFormatting.AQUA + "Diamond";
        if (item == Items.emerald) return EnumChatFormatting.DARK_GREEN + "Emerald";
        return "Unknown Item";
    }

    private EnumChatFormatting getItemColor(Item item) {
        if (item == Items.iron_ingot) return EnumChatFormatting.WHITE;
        if (item == Items.gold_ingot) return EnumChatFormatting.GOLD;
        if (item == Items.diamond) return EnumChatFormatting.AQUA;
        if (item == Items.emerald) return EnumChatFormatting.DARK_GREEN;
        return EnumChatFormatting.WHITE;
    }

    private boolean shouldTrack(Item item) {
        if (item == Items.iron_ingot) return this.iron;
        if (item == Items.gold_ingot) return this.gold;
        if (item == Items.diamond) return this.diamond;
        if (item == Items.emerald) return this.emerald;
        return false;
    }

    private static void sendStackedMessage(String text, int id) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        ChatComponentText chatComponentText = new ChatComponentText(text);
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chatComponentText, id);
    }

    private int setChatId(Item item) {
        return 5000 + Item.getIdFromItem(item);
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("XXX + 99 + 99", 6, this.scale)));
    }

    @Override
    public void onReset() {
        INVENTORY_RESOURCES.clear();
        CHEST_RESOURCES.clear();
    }
}