package wtf.tatp.meowtils.module.skywars;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.SlotClickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Util;

public class ItemHighlight extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public float opacity = 50.0F;
    @Config
    public String blacklistColor = "§4Dark Red";
    @Config
    public String safelistColor = "§aGreen";
    @Config
    public String bestColor = "§dLight Purple";
    @Config
    public boolean showBlacklisted = true;
    @Config
    public boolean showSafelisted = true;
    @Config
    public boolean showBest = true;
    @Config
    public boolean swords = true;
    @Config
    public boolean armor = true;
    @Config
    public boolean healing = true;
    @Config
    public boolean bows = true;
    @Config
    public boolean tools = true;
    @Config
    public boolean preventDropSafelisted = false;
    @Config
    public boolean preventDropBest = true;

    private static final File BLACKLIST = Meowtils.ITEMHIGHLIGHT_BLACKLIST;
    private static final File SAFELIST = Meowtils.ITEMHIGHLIGHT_SAFELIST;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Set<String>>() {}.getType();
    private static Set<String> safelist = new HashSet<>();
    private static Set<String> blacklist = new HashSet<>();

    private static ItemStack bestSword;
    private static int bestSwordScore;
    private static int bestSwordAny;
    private static ItemStack bestBow;
    private static int bestBowScore;
    private static int bestBowAny;
    private static final ItemStack[] bestArmor = new ItemStack[4];
    private static final int[] bestArmorScore = new int[4];
    private static final int[] bestArmorAny = new int[4];
    private static ItemStack bestPickaxe;
    private static int bestPickaxeSharp;
    private static int bestPickaxeEff;
    private static int bestPickaxeAny;
    private static ItemStack bestAxe;
    private static int bestAxeSharp;
    private static int bestAxeEff;
    private static int bestAxeAny;
    private static ItemStack bestShovel;
    private static int bestShovelSharp;
    private static int bestShovelEff;
    private static int bestShovelAny;
    private static ItemStack bestHoe;
    private static int bestHoeSharp;
    private static int bestHoeEff;
    private static int bestHoeAny;

    private static final List<String> DEFAULT_BLACKLIST = Arrays.asList(
            "chest", "double_plant", "feather", "fireworks", "glass_bottle", "gunpowder",
            "jukebox", "leather", "lever", "magma_cream", "noteblock", "prismarine_crystals",
            "prismarine_shard", "rabbit_foot", "rabbit_hide", "record_11", "record_13",
            "record_blocks", "record_cat", "record_chirp", "record_far", "record_mall",
            "record_mellohi", "record_stal", "record_strad", "record_wait", "record_ward",
            "red_flower", "redstone", "redstone_torch", "repeater", "rotten_flesh", "saddle",
            "sand", "sapling", "spider_eye", "stone_button", "stone_pressure_plate", "string",
            "torch", "tripwire_hook", "waterlily", "wheat", "wooden_button", "wooden_pressure_plate",
            "yellow_flower", "carrot_on_a_stick", "cactus", "wheat_seeds", "skull", "gravel",
            "oak_stairs", "stone_stairs", "brick_stairs", "stone_brick_stairs", "nether_brick_stairs",
            "sandstone_stairs", "spruce_stairs", "birch_stairs", "jungle_stairs", "quartz_stairs",
            "acacia_stairs", "dark_oak_stairs", "red_sandstone_stairs", "stone_slab", "wooden_slab",
            "stone_slab2", "wooden_pickaxe", "wooden_shovel", "wooden_hoe"
    );

    private static final List<String> DEFAULT_SAFELIST = Arrays.asList(
            "arrow", "bow", "chainmail_boots", "chainmail_chestplate", "chainmail_helmet",
            "chainmail_leggings", "clock", "diamond", "diamond_axe", "diamond_pickaxe",
            "diamond_shovel", "diamond_hoe", "diamond_block", "diamond_boots", "diamond_chestplate",
            "diamond_helmet", "diamond_leggings", "diamond_sword", "ender_pearl", "fishing_rod",
            "golden_apple", "golden_axe", "golden_boots", "golden_chestplate", "golden_helmet",
            "golden_leggings", "golden_sword", "iron_axe", "iron_pickaxe", "iron_shovel",
            "iron_hoe", "iron_boots", "iron_chestplate", "iron_helmet", "iron_leggings",
            "iron_sword", "leather_boots", "leather_chestplate", "leather_helmet", "leather_leggings",
            "snowball", "stone_axe", "stone_sword", "wooden_axe", "wooden_sword", "egg",
            "lava_bucket", "water_bucket", "potion"
    );

    public ItemHighlight() {
        super("ItemHighlight", Module.Category.Skywars);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Highlights important items in your inventory.\n§d/itemsl §d<item> §f- safelist\n§d/itemusl §d<item> §f- unsafelist\n§d/itembl §d<item> §f- blacklist\n§d/itemubl §d<item> §f- unblacklist");
        addOpacity(new OpacityValue("Opacity", "opacity", this));
        addMode(new ModeValue("Blacklist", Arrays.asList("§4Dark Red", "§cRed", "§eYellow", "§1Dark Blue"), "blacklistColor", this));
        addMode(new ModeValue("Safelist", Arrays.asList("§aGreen", "§2Dark Green", "§9Blue", "§3Dark Aqua"), "safelistColor", this));
        addMode(new ModeValue("Best", Arrays.asList("§dLight Purple", "§5Dark Purple", "§6Gold", "§bAqua"), "bestColor", this));
        addToggle(new ToggleValue("Prevent drop for best", "preventDropBest", this));
        addToggle(new ToggleValue("Prevent drop for safelisted", "preventDropSafelisted", this));
        addToggle(new ToggleValue("Render §4Blacklisted §fItems", "showBlacklisted", this));
        addToggle(new ToggleValue("Render §aSafelisted §fItems", "showSafelisted", this));
        addToggle(new ToggleValue("Render §dBest §fItems", "showBest", this));
        addExpand(new ExpandValue("Items", e -> {
            e.addCheck(new CheckValue("§bSwords", "swords", this));
            e.addCheck(new CheckValue("§3Armor", "armor", this));
            e.addCheck(new CheckValue("§dHealing", "healing", this));
            e.addCheck(new CheckValue("§6Bows", "bows", this));
            e.addCheck(new CheckValue("§2Tools", "tools", this));
        }));
    }

    public static void init() {
        load();
    }

    public static String getListName(ItemStack stack) {
        return Item.itemRegistry.getNameForObject(stack.getItem()).toString().replaceAll("minecraft:", "");
    }

    public static boolean shouldHighlight(ItemStack stack) {
        if (stack == null) return false;
        if (!isSafelisted(getListName(stack))) return false;
        ItemHighlight s = Module.get(ItemHighlight.class);
        if (s == null) return false;

        update();

        if (s.swords && stack == bestSword) return true;
        if (s.bows && stack == bestBow) return true;
        if (s.healing && isHealingItem(stack)) return true;
        if (s.tools && (stack == bestPickaxe || stack == bestAxe || stack == bestShovel || stack == bestHoe)) return true;

        if (s.armor)
            for (ItemStack armor : bestArmor) {
                if (stack == armor) return true;
            }
        return false;
    }

    public static int getColor(String config) {
        if (config == null || config.isEmpty()) return ColorUtil.getRGBFromFormatting(EnumChatFormatting.WHITE);
        config = config.replace(" ", "_").replaceAll("§.", "");

        EnumChatFormatting enumColor = ColorUtil.getColorFromString(config);
        Color color = ColorUtil.getColorFromFormatting(enumColor);

        ItemHighlight itemHighlight = Module.get(ItemHighlight.class);
        float opacityValue = itemHighlight != null ? itemHighlight.opacity : 50.0F;
        return ColorUtil.rgba(color.getRed(), color.getGreen(), color.getBlue(), ColorUtil.convertOpacity(opacityValue));
    }

    @EventTarget
    public void onSlotClick(SlotClickEvent event) {
        if (event.getClickType() == 4 && (Skywars.GAME.isActive() || Skywars.MINI.isActive())) {
            if (!event.getSlot().getHasStack()) return;

            if (this.preventDropBest && shouldHighlight(event.getSlot().getStack())) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Stopped you from dropping best item!");
                Util.playSound(Util.Sound.ERROR_DEEP, 100);
                event.setCancelled(true);
                return;
            }
            if (this.preventDropSafelisted && isSafelisted(getListName(event.getSlot().getStack()))) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Stopped you from dropping safelisted item!");
                Util.playSound(Util.Sound.ERROR_DEEP, 100);
                event.setCancelled(true);
            }
        }
    }

    private static void update() {
        Minecraft mc = Minecraft.getMinecraft();
        clear();

        for (int i = 0; i < 4; i++) {
            compare(mc.thePlayer.inventory.armorInventory[i]);
        }

        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            compare(stack);
        }

        if (mc.thePlayer.openContainer != mc.thePlayer.inventoryContainer) {
            for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
                if (slot != null) {
                    compare(slot.getStack());
                }
            }
        }
    }

    private static void compare(ItemStack stack) {
        if (stack == null) return;
        Item item = stack.getItem();

        if (item instanceof ItemSword) {
            int dmg = (int) (((ItemSword) item).getDamageVsEntity() + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack));
            int any = stack.hasDisplayName() ? 1 : 0;

            if (bestSword == null || dmg > bestSwordScore || (dmg == bestSwordScore && any > bestSwordAny)) {
                bestSwordScore = dmg;
                bestSwordAny = any;
                bestSword = stack;
            }
        } else if (item instanceof ItemBow) {
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int any = stack.hasDisplayName() ? 1 : 0;

            if (bestBow == null || power > bestBowScore || (power == bestBowScore && any > bestBowAny)) {
                bestBowScore = power;
                bestBowAny = any;
                bestBow = stack;
            }
        } else if (item instanceof ItemPickaxe) {
            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int eff = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            int any = stack.hasDisplayName() ? 1 : 0;

            if (bestPickaxe == null || sharp > bestPickaxeSharp || (sharp == bestPickaxeSharp && eff > bestPickaxeEff) || (sharp == bestPickaxeSharp && eff == bestPickaxeEff && any > bestPickaxeAny)) {
                bestPickaxeSharp = sharp;
                bestPickaxeEff = eff;
                bestPickaxeAny = any;
                bestPickaxe = stack;
            }
        } else if (item instanceof ItemAxe) {
            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int eff = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            int any = stack.hasDisplayName() ? 1 : 0;

            if (bestAxe == null || sharp > bestAxeSharp || (sharp == bestAxeSharp && eff > bestAxeEff) || (sharp == bestAxeSharp && eff == bestAxeEff && any > bestAxeAny)) {
                bestAxeSharp = sharp;
                bestAxeEff = eff;
                bestAxeAny = any;
                bestAxe = stack;
            }
        } else if (item instanceof ItemSpade) {
            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int eff = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            int any = stack.hasDisplayName() ? 1 : 0;

            if (bestShovel == null || sharp > bestShovelSharp || (sharp == bestShovelSharp && eff > bestShovelEff) || (sharp == bestShovelSharp && eff == bestShovelEff && any > bestShovelAny)) {
                bestShovelSharp = sharp;
                bestShovelEff = eff;
                bestShovelAny = any;
                bestShovel = stack;
            }
        } else if (item instanceof ItemHoe) {
            int sharp = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int eff = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            int any = stack.hasDisplayName() ? 1 : 0;

            if (bestHoe == null || sharp > bestHoeSharp || (sharp == bestHoeSharp && eff > bestHoeEff) || (sharp == bestHoeSharp && eff == bestHoeEff && any > bestHoeAny)) {
                bestHoeSharp = sharp;
                bestHoeEff = eff;
                bestHoeAny = any;
                bestHoe = stack;
            }
        } else if (item instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) item;
            int slot = armor.armorType;
            int score = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int any = stack.hasDisplayName() ? 1 : 0;

            if (score > bestArmorScore[slot] || (score == bestArmorScore[slot] && any > bestArmorAny[slot])) {
                bestArmorScore[slot] = score;
                bestArmorAny[slot] = any;
                bestArmor[slot] = stack;
            }
        }
    }

    private static boolean isHealingItem(ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() instanceof ItemAppleGold;
    }

    private static void clear() {
        bestSword = bestBow = bestPickaxe = bestAxe = bestShovel = bestHoe = null;
        bestSwordScore = bestSwordAny = 0;
        bestBowScore = bestBowAny = 0;
        bestPickaxeSharp = bestPickaxeEff = bestPickaxeAny = 0;
        bestAxeSharp = bestAxeEff = bestAxeAny = 0;
        bestShovelSharp = bestShovelEff = bestShovelAny = 0;
        bestHoeSharp = bestHoeEff = bestHoeAny = 0;

        for (int i = 0; i < 4; i++) {
            bestArmor[i] = null;
            bestArmorAny[i] = 0;
            bestArmorScore[i] = 0;
        }
    }

    public static void addSafelistItem(String id) {
        id = id.toLowerCase();
        safelist.add(id);
        save();
    }

    public static void removeSafelistItem(String id) {
        id = id.toLowerCase();
        safelist.remove(id);
        save();
    }

    public static void addBlacklistItem(String id) {
        id = id.toLowerCase();
        blacklist.add(id);
        save();
    }

    public static void removeBlacklistItem(String id) {
        id = id.toLowerCase();
        blacklist.remove(id);
        save();
    }

    public static boolean isSafelisted(String id) {
        id = id.toLowerCase();
        return safelist.contains(id);
    }

    public static boolean isBlacklisted(String id) {
        id = id.toLowerCase();
        return blacklist.contains(id);
    }

    private static void load() {
        boolean initSafelist = (!SAFELIST.exists() || SAFELIST.length() == 0L);
        boolean initBlacklist = (!BLACKLIST.exists() || BLACKLIST.length() == 0L);

        if (initSafelist) {
            safelist.clear();
            safelist.addAll(DEFAULT_SAFELIST);
        } else {
            readSafelist();
        }

        if (initBlacklist) {
            blacklist.clear();
            blacklist.addAll(DEFAULT_BLACKLIST);
        } else {
            readBlacklist();
        }

        if (initBlacklist || initSafelist) {
            save();
        }
    }

    private static void readSafelist() {
        try (Reader reader = new FileReader(SAFELIST)) {
            Set<String> data = gson.fromJson(reader, TYPE);
            safelist = (data != null) ? data : new HashSet<>();
        } catch (Exception e) {
            e.printStackTrace();
            safelist = new HashSet<>();
        }
    }

    private static void readBlacklist() {
        try (Reader reader = new FileReader(BLACKLIST)) {
            Set<String> data = gson.fromJson(reader, TYPE);
            blacklist = (data != null) ? data : new HashSet<>();
        } catch (Exception e) {
            e.printStackTrace();
            blacklist = new HashSet<>();
        }
    }

    private static void save() {
        try (Writer writer = new FileWriter(SAFELIST)) {
            gson.toJson(safelist, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = new FileWriter(BLACKLIST)) {
            gson.toJson(blacklist, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}