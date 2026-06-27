package wtf.tatp.meowtils.module.bedwars;

import java.awt.Color;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.SlotClickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.mixin.AccessorGuiChest;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Util;

public class ShopHelper extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean highlightAffordable = true;
    @Config
    public float opacity = 50.0F;
    @Config
    public boolean replaceClicks = true;
    @Config
    public boolean preventDuplicate = true;

    public static final Map<Item, Integer> INVENTORY_RESOURCES = new HashMap<>();
    private static final Map<Item, Integer> CURRENT_RESOURCES = new HashMap<>();
    private static final Map<Item, ItemCategory> CATEGORY = new HashMap<>();
    private static final Map<Item, Integer> PRIORITY = new HashMap<>();
    private static final EnumMap<ItemCategory, Integer> BEST_ITEMS = new EnumMap<>(ItemCategory.class);
    private static final Color IRON_COLOR = ColorUtil.getColorFromFormatting(EnumChatFormatting.WHITE);
    private static final Color GOLD_COLOR = ColorUtil.getColorFromFormatting(EnumChatFormatting.GOLD);
    private static final Color DIAMOND_COLOR = ColorUtil.getColorFromFormatting(EnumChatFormatting.AQUA);
    private static final Color EMERALD_COLOR = ColorUtil.getColorFromFormatting(EnumChatFormatting.DARK_GREEN);

    private static final String[] BEDWARS_SHOP = new String[] {
            "Quick Buy", "Blocks", "Melee", "Armor", "Tools", "Ranged",
            "Potions", "Utility", "Rotating Items", "Upgrades & Traps"
    };

    public ShopHelper() {
        super("ShopHelper", Module.Category.Bedwars);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Helps you shop faster by highlighting affordable items, replacing clicks with middle clicks and\npreventing purchasing certain duplicate items.");
        addOpacity(new OpacityValue("Opacity", "opacity", this));
        addToggle(new ToggleValue("Highlight affordable", "highlightAffordable", this));
        addToggle(new ToggleValue("Replace clicks", "replaceClicks", this));
        addToggle(new ToggleValue("Prevent duplicate", "preventDuplicate", this));
    }

    static {
        register(ItemCategory.SWORD, Items.diamond_sword, Items.iron_sword, Items.stone_sword);
        register(ItemCategory.ARMOR, Items.chainmail_boots, Items.chainmail_chestplate, Items.chainmail_helmet);
        register(ItemCategory.PICKAXE, Items.diamond_pickaxe, Items.iron_pickaxe, Items.golden_pickaxe, Items.stone_pickaxe);
        register(ItemCategory.AXE, Items.diamond_axe, Items.iron_axe, Items.golden_axe, Items.stone_axe);
        register(ItemCategory.STICK, Items.stick);
        register(ItemCategory.SHEARS, Items.shears);
    }

    private enum ItemCategory {
        SWORD,
        ARMOR,
        PICKAXE,
        AXE,
        STICK,
        SHEARS;
    }

    public static int getColor(Item item) {
        Color color = ColorUtil.getColorFromFormatting(EnumChatFormatting.GRAY);
        if (item == Items.iron_ingot) {
            color = IRON_COLOR;
        } else if (item == Items.gold_ingot) {
            color = GOLD_COLOR;
        } else if (item == Items.diamond) {
            color = DIAMOND_COLOR;
        } else if (item == Items.emerald) {
            color = EMERALD_COLOR;
        }

        ShopHelper shopHelper = Module.get(ShopHelper.class);
        float opacityValue = shopHelper != null ? shopHelper.opacity : 50.0F;
        return ColorUtil.rgba(color.getRed(), color.getGreen(), color.getBlue(), ColorUtil.convertOpacity(opacityValue));
    }

    @EventTarget
    public void onSlotClick(SlotClickEvent event) {
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;
        if (!(event.getGuiContainer() instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) event.getGuiContainer();
        String title = ((AccessorGuiChest) chest).getLowerChestInventory().getName();
        if (Arrays.asList(BEDWARS_SHOP).contains(title) && !title.contains("Upgrades & Traps")) {
            if (!event.getSlot().getHasStack()) return;

            ItemStack stack = event.getSlot().getStack();
            Item item = stack.getItem();
            ItemCost cost = getCostFromLore(stack);

            if (this.preventDuplicate && !shouldHighlight(stack, cost) &&
                    (item instanceof ItemSword || item == Items.stick) && item != Items.wooden_sword) {
                event.setCancelled(true);

                if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "Prevented you from buying duplicate item!");
                }

                if (Notifications.getMode() != Notifications.Mode.CHAT) {
                    NotificationManager.show("ShopHelper", "Prevented duplicate!", NotificationManager.Type.INFO, 1500L);
                }

                Util.playSound(Util.Sound.ERROR, 100);
            }
        }

        if (Arrays.asList(BEDWARS_SHOP).contains(title) && this.replaceClicks && event.getClickType() == 0) {
            event.setReplaceClick();
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (!this.highlightAffordable) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;

        CURRENT_RESOURCES.clear();
        BEST_ITEMS.clear();

        if (!(this.mc.currentScreen instanceof GuiChest)) return;

        GuiChest chest = (GuiChest) this.mc.currentScreen;
        String title = ((AccessorGuiChest) chest).getLowerChestInventory().getName();

        for (String s : BEDWARS_SHOP) {
            if (title.equals(s)) {
                for (ItemStack stack : this.mc.thePlayer.inventory.mainInventory) {
                    if (stack != null) {
                        if (shouldTrack(stack.getItem())) {
                            CURRENT_RESOURCES.put(stack.getItem(), CURRENT_RESOURCES.getOrDefault(stack.getItem(), 0) + stack.stackSize);
                        }

                        ItemCategory category = CATEGORY.get(stack.getItem());
                        if (category != null) {
                            int priority = PRIORITY.get(stack.getItem());
                            BEST_ITEMS.merge(category, priority, Math::min);
                        }
                    }
                }

                INVENTORY_RESOURCES.clear();
                INVENTORY_RESOURCES.putAll(CURRENT_RESOURCES);
            }
        }
    }

    public static ItemCost getCostFromLore(ItemStack stack) {
        if (stack == null) return null;

        for (String line : stack.getTooltip(Minecraft.getMinecraft().thePlayer, false)) {
            String clean = EnumChatFormatting.getTextWithoutFormattingCodes(line);
            String[] split = clean.split(" ");

            if ((!clean.contains("Cost:") && !clean.contains("Tier")) || split.length < 3) {
                continue;
            }

            int amount;
            String type;

            if (clean.contains("Cost:")) {
                if (!isNumeric(split[1])) continue;
                amount = Integer.parseInt(split[1]);
                type = split[2].toLowerCase();
            } else {
                int commaIndex = clean.lastIndexOf(',');
                if (commaIndex == -1 || commaIndex + 2 >= clean.length()) continue;
                String costPart = clean.substring(commaIndex + 2);
                String[] costSplit = costPart.split(" ");
                if (costSplit.length < 2) continue;
                if (!isNumeric(costSplit[0])) continue;
                amount = Integer.parseInt(costSplit[0]);
                type = costSplit[1].toLowerCase();
            }

            Item item = type.startsWith("iron") ? Items.iron_ingot :
                    (type.startsWith("gold") ? Items.gold_ingot :
                     (type.startsWith("diamond") || type.startsWith("diamonds") ? Items.diamond :
                      (type.startsWith("emerald") ? Items.emerald : null)));

            if (type.contains("unlocked")) return null;
            if (item != null) {
                return new ItemCost(item, amount);
            }
        }
        return null;
    }

    public static boolean shouldHighlight(ItemStack stack, ItemCost cost) {
        Minecraft mc = Minecraft.getMinecraft();
        if (stack == null || cost == null) return false;

        int available = INVENTORY_RESOURCES.getOrDefault(cost.resourceType, 0);
        boolean affordable = (available >= cost.amount);

        if (mc.currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) mc.currentScreen;
            String title = ((AccessorGuiChest) chest).getLowerChestInventory().getName();

            if (title.contains("Upgrades & Traps")) {
                return affordable;
            }
        }

        if (cost.resourceType == Items.diamond) {
            return affordable;
        }

        Item item = stack.getItem();
        ItemCategory category = CATEGORY.get(item);
        if (category != null) {
            int priority = PRIORITY.get(item);
            int bestItem = BEST_ITEMS.getOrDefault(category, Integer.MAX_VALUE);
            return (priority < bestItem && affordable);
        }
        return affordable;
    }

    private static boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    private static void register(ItemCategory category, Item... items) {
        for (int i = 0; i < items.length; i++) {
            CATEGORY.put(items[i], category);
            PRIORITY.put(items[i], i);
        }
    }

    private boolean shouldTrack(Item item) {
        if (item == Items.iron_ingot) return true;
        if (item == Items.gold_ingot) return true;
        if (item == Items.diamond) return true;
        if (item == Items.emerald) return true;
        return false;
    }

    public static class ItemCost {
        public final Item resourceType;
        public final int amount;

        public ItemCost(Item resourceType, int amount) {
            this.resourceType = resourceType;
            this.amount = amount;
        }
    }
}