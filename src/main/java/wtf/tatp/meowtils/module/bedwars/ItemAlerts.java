package wtf.tatp.meowtils.module.bedwars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.module.hypixel.PartyNotifier;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class ItemAlerts extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public String sound = "None";
    @Config
    public String distanceMode = "Important";
    @Config
    public String alertType = "Chat";
    @Config
    public int cooldown = 10;
    @Config
    public boolean ironSword = true;
    @Config
    public boolean diamondSword = true;
    @Config
    public boolean bows = true;
    @Config
    public boolean knockbackStick = true;
    @Config
    public boolean goldenApple = true;
    @Config
    public boolean waterBucket = true;
    @Config
    public boolean goldenPickaxe = true;
    @Config
    public boolean diamondPickaxe = true;
    @Config
    public boolean fireball = true;
    @Config
    public boolean tnt = true;
    @Config
    public boolean milk = true;
    @Config
    public boolean jump = true;
    @Config
    public boolean speed = true;
    @Config
    public boolean invis = true;
    @Config
    public boolean pearl = true;
    @Config
    public boolean obsidian = true;
    @Config
    public boolean egg = true;
    @Config
    public boolean golem = true;
    @Config
    public boolean bedbug = true;
    @Config
    public boolean tower = true;
    @Config
    public boolean rotation = true;

    private static final Map<String, Map<String, Long>> COOLDOWNS = new HashMap<>();

    public ItemAlerts() {
        super("ItemAlerts", Module.Category.Bedwars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts in chat when a player holds a specific item.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addMode(new ModeValue("Ping sound", Arrays.asList("All", "Important", "None"), "sound", this));
        addMode(new ModeValue("Distance", Arrays.asList("All", "Important", "None"), "distanceMode", this));
        addSlider(new SliderValue("Cooldown", 1.0D, 30.0D, 1.0D, "s", "cooldown", this, int.class));
        addExpand(new ExpandValue("Items", e -> {
            e.addCheck(new CheckValue("Iron Sword", "ironSword", this));
            e.addCheck(new CheckValue("§bDiamond Sword", "diamondSword", this));
            e.addCheck(new CheckValue("§6Bows", "bows", this));
            e.addCheck(new CheckValue("§eKnockback Stick", "knockbackStick", this));
            e.addCheck(new CheckValue("§6Golden Apple", "goldenApple", this));
            e.addCheck(new CheckValue("§9Water bucket", "waterBucket", this));
            e.addCheck(new CheckValue("§6Golden Pickaxe", "goldenPickaxe", this));
            e.addCheck(new CheckValue("§bDiamond Pickaxe", "diamondPickaxe", this));
            e.addCheck(new CheckValue("§cFireball", "fireball", this));
            e.addCheck(new CheckValue("§4T§fN§4T", "tnt", this));
            e.addCheck(new CheckValue("§3Pop-Up Tower", "tower", this));
            e.addCheck(new CheckValue("Milk", "milk", this));
            e.addCheck(new CheckValue("§aJump Potion", "jump", this));
            e.addCheck(new CheckValue("§eSpeed Potion", "speed", this));
            e.addCheck(new CheckValue("§bInvis Potion", "invis", this));
            e.addCheck(new CheckValue("§5Ender Pearl", "pearl", this));
            e.addCheck(new CheckValue("§8Obsidian", "obsidian", this));
            e.addCheck(new CheckValue("§3Bridge Egg", "egg", this));
            e.addCheck(new CheckValue("Iron Golem", "golem", this));
            e.addCheck(new CheckValue("§7BedBug", "bedbug", this));
            e.addCheck(new CheckValue("§dRotation Items", "rotation", this));
        }));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive()) return;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == null || player == this.mc.thePlayer ||
                    TeamUtil.isTeam(player) ||
                    TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName()))
                continue;

            ItemStack held = player.getHeldItem();
            String itemName = null;
            if (held == null) continue;

            if (held.getItem() == Items.iron_sword && this.ironSword) {
                itemName = EnumChatFormatting.WHITE + "Iron Sword";
            }
            if (held.getItem() == Items.diamond_sword && this.diamondSword) {
                itemName = EnumChatFormatting.AQUA + "Diamond Sword";
            }
            if (held.getItem() == Items.golden_apple && this.goldenApple) {
                itemName = EnumChatFormatting.GOLD + "Golden Apple";
            }
            if (held.getItem() == Items.bow && held.isItemEnchanted() && this.bows) {
                itemName = EnumChatFormatting.GOLD + "Enchanted Bow";
            } else if (held.getItem() == Items.bow) {
                itemName = EnumChatFormatting.GOLD + "Bow";
            }
            if (held.getItem() == Items.stick && this.knockbackStick) {
                itemName = EnumChatFormatting.GOLD + "Knockback Stick";
            }
            if (held.getItem() == Items.bucket && this.waterBucket) {
                itemName = EnumChatFormatting.BLUE + "Water Bucket";
            }
            if (held.getItem() == Items.diamond_pickaxe && this.diamondPickaxe) {
                itemName = EnumChatFormatting.AQUA + "Diamond Pickaxe";
            }
            if (held.getItem() == Items.iron_pickaxe && this.goldenPickaxe) {
                itemName = EnumChatFormatting.GOLD + "Golden Pickaxe";
            }
            if (held.getItem() == Items.fire_charge && this.fireball) {
                itemName = EnumChatFormatting.RED + "Fireball";
            }
            if (held.getItem() == Item.getItemFromBlock(Blocks.tnt) && !held.isItemEnchanted() && this.tnt) {
                itemName = EnumChatFormatting.RED + "T" + EnumChatFormatting.WHITE + "N" + EnumChatFormatting.RED + "T";
            }
            if (held.getItem() == Items.milk_bucket && this.milk) {
                itemName = EnumChatFormatting.WHITE + "Milk";
            }
            if (held.getDisplayName().toLowerCase().contains("jump") && this.jump) {
                itemName = EnumChatFormatting.GREEN + "Jump Potion";
            }
            if (held.getDisplayName().toLowerCase().contains("speed") && this.speed) {
                itemName = EnumChatFormatting.YELLOW + "Speed Potion";
            }
            if (held.getDisplayName().toLowerCase().contains("invis") && this.invis) {
                itemName = EnumChatFormatting.AQUA + "Invis Potion";
            }
            if (held.getItem() == Items.ender_pearl && !held.isItemEnchanted() && this.pearl) {
                itemName = EnumChatFormatting.DARK_PURPLE + "Ender Pearl";
            }
            if (held.getItem() == Items.egg && this.egg) {
                itemName = EnumChatFormatting.DARK_AQUA + "Bridge Egg";
            }
            if (held.getItem() == Item.getItemFromBlock(Blocks.obsidian) && this.obsidian) {
                itemName = EnumChatFormatting.DARK_GRAY + "Obsidian";
            }
            if (held.getItem() == Items.snowball && !held.isItemEnchanted() && this.bedbug) {
                itemName = EnumChatFormatting.WHITE + "Bedbug";
            }
            if (held.getItem() == Items.spawn_egg && held.getMetadata() != 93 && this.golem) {
                itemName = EnumChatFormatting.WHITE + "Iron Golem";
            }
            if (held.getItem() == Item.getItemFromBlock(Blocks.chest) && this.tower) {
                itemName = EnumChatFormatting.DARK_AQUA + "Pop-Up Tower";
            }

            if (this.rotation) {
                if (held.getItem() == Items.nether_star) {
                    itemName = EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.UNDERLINE + "Shuriken";
                } else if (held.getItem() == Item.getItemFromBlock(Blocks.packed_ice) && held.isItemEnchanted()) {
                    itemName = EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.UNDERLINE + "Ice Bridge";
                } else if (held.getItem() == Items.fishing_rod) {
                    itemName = EnumChatFormatting.DARK_BLUE.toString() + EnumChatFormatting.UNDERLINE + "Bridge Zapper";
                } else if (held.getItem() == Items.diamond_horse_armor) {
                    itemName = EnumChatFormatting.GOLD.toString() + EnumChatFormatting.UNDERLINE + "Charlie the Unicorn";
                } else if (held.getItem() == Items.ender_pearl && held.isItemEnchanted()) {
                    itemName = EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.UNDERLINE + "Time Warp Pearl";
                } else if (held.getItem() == Items.cookie) {
                    itemName = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.UNDERLINE + "Sugar Cookie";
                } else if (held.getItem() == Item.getItemFromBlock(Blocks.web)) {
                    itemName = EnumChatFormatting.WHITE.toString() + EnumChatFormatting.UNDERLINE + "Cobweb";
                } else if (held.getItem() == Item.getItemFromBlock(Blocks.ender_chest)) {
                    itemName = EnumChatFormatting.DARK_GREEN.toString() + EnumChatFormatting.UNDERLINE + "Teleportation Device";
                } else if (held.getItem() == Item.getItemFromBlock(Blocks.tnt) && held.isItemEnchanted()) {
                    itemName = EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.UNDERLINE + "Mega " + EnumChatFormatting.RED + "T" + EnumChatFormatting.WHITE + "N" + EnumChatFormatting.RED + "T";
                } else if (held.getItem() == Items.bone) {
                    itemName = EnumChatFormatting.GOLD.toString() + EnumChatFormatting.UNDERLINE + "Mace";
                } else if (held.getItem() == Items.snowball && held.isItemEnchanted()) {
                    itemName = EnumChatFormatting.WHITE.toString() + EnumChatFormatting.UNDERLINE + "Wind Charge";
                } else if (held.getItem() == Items.shears && held.isItemEnchanted()) {
                    itemName = EnumChatFormatting.GREEN.toString() + EnumChatFormatting.UNDERLINE + "Enchanted Shears";
                } else if (held.getItem() == Item.getItemFromBlock(Blocks.beacon) && !held.isItemEnchanted()) {
                    itemName = EnumChatFormatting.RED.toString() + EnumChatFormatting.UNDERLINE + "Final Revive Beacon";
                } else if (held.getItem() == Items.prismarine_crystals) {
                    itemName = EnumChatFormatting.AQUA.toString() + EnumChatFormatting.UNDERLINE + "Block Zapper";
                }
            }

            if (itemName != null) {
                if (!hasCooldown(player.getName(), itemName)) {
                    alert(player, itemName);
                    setCooldown(player.getName(), itemName);
                }
            }
        }
    }

    private void alert(EntityPlayer player, String itemName) {
        int distanceToEntity = (int) player.getDistanceToEntity(this.mc.thePlayer);
        String distance = EnumChatFormatting.GRAY + " (" + EnumChatFormatting.AQUA + distanceToEntity + "m" + EnumChatFormatting.GRAY + ")";
        String rawItemName = ColorUtil.unformattedText(itemName).toLowerCase();

        String showDistance = this.distanceMode.equals("All") ? distance :
                ((this.distanceMode.equals("Important") && (rawItemName.equalsIgnoreCase("fireball") ||
                        rawItemName.equalsIgnoreCase("ender pearl") || rawItemName.equalsIgnoreCase("bridge egg") ||
                        rawItemName.equalsIgnoreCase("speed potion") || rawItemName.equalsIgnoreCase("jump potion") ||
                        rawItemName.equalsIgnoreCase("invis potion") || rawItemName.equalsIgnoreCase("charlie the unicorn"))) ? distance : "");

        if (!this.alertType.equals("Chat")) {
            NotificationManager.show(NameUtil.getTabDisplayName(player.getName()), itemName, NotificationManager.Type.ALERT, 1500L);
        }
        if (!this.alertType.equals("Notification")) {
            Meowtils.addMessage(NameUtil.getTabDisplayName(player.getName()) + EnumChatFormatting.GRAY + " has " + itemName + showDistance);
        }

        PartyNotifier.itemAlerts(player.getName(), ColorUtil.unformattedText(itemName));

        if (this.sound.equals("All")) {
            sound();
        } else if (this.sound.equals("Important") && (rawItemName.equalsIgnoreCase("jump potion") ||
                rawItemName.equalsIgnoreCase("speed potion") || rawItemName.equalsIgnoreCase("invis potion") ||
                rawItemName.equalsIgnoreCase("bridge egg") || rawItemName.equalsIgnoreCase("diamond sword") ||
                rawItemName.equalsIgnoreCase("iron golem") || rawItemName.equalsIgnoreCase("bedbug") ||
                rawItemName.equalsIgnoreCase("charlie the unicorn") || rawItemName.equalsIgnoreCase("diamond pickaxe") ||
                rawItemName.equalsIgnoreCase("enchanted bow") || rawItemName.equalsIgnoreCase("milk") ||
                rawItemName.equalsIgnoreCase("ender pearl"))) {
            sound();
        }
    }

    private boolean hasCooldown(String playerName, String itemName) {
        long ALERT_COOLDOWN = this.cooldown * 1000L;
        Map<String, Long> playerCooldowns = COOLDOWNS.get(playerName);
        if (playerCooldowns == null) return false;

        Long lastTime = playerCooldowns.get(itemName);
        return (lastTime != null && System.currentTimeMillis() - lastTime < ALERT_COOLDOWN);
    }

    private void setCooldown(String playerName, String itemName) {
        COOLDOWNS.computeIfAbsent(playerName, k -> new HashMap<>()).put(itemName, System.currentTimeMillis());
    }

    private void sound() {
        Util.playSound(Util.Sound.PING, 100);
    }

    @Override
    public void onReset() {
        COOLDOWNS.clear();
    }
}