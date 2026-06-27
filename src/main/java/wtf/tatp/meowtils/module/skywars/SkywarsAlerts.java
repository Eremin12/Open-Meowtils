package wtf.tatp.meowtils.module.skywars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
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
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class SkywarsAlerts extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public int cooldown = 10;
    @Config
    public String soundMode = "None";
    @Config
    public boolean showDistance = true;
    @Config
    public boolean fireSword = true;
    @Config
    public boolean diamondSword = true;
    @Config
    public boolean knockbackSword = true;
    @Config
    public boolean knockbackRod = true;
    @Config
    public boolean strengthPotion = true;
    @Config
    public boolean enderPearl = true;
    @Config
    public boolean corruptPearl = true;
    @Config
    public boolean warpPearl = true;
    @Config
    public String alertType = "Chat";
    @Config
    public boolean nametagIcon = true;
    @Config
    public boolean swordIcon = true;
    @Config
    public boolean knockbackIcon = true;
    @Config
    public boolean pearlIcon = true;
    @Config
    public boolean strengthIcon = true;

    private static final Map<String, Map<String, Long>> COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Set<Item>> HELD_ITEM_CACHE = new HashMap<>();

    public SkywarsAlerts() {
        super("SkywarsAlerts", Module.Category.Skywars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts you of items players have in Skywars.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addMode(new ModeValue("Ping sound", Arrays.asList("All", "Important", "None"), "soundMode", this));
        addSlider(new SliderValue("Cooldown", 1.0D, 30.0D, 1.0D, "s", "cooldown", this, int.class));
        addToggle(new ToggleValue("Show distance", "showDistance", this));
        addExpand(new ExpandValue("Items", e -> {
            e.addCheck(new CheckValue("§cFire §fSword", "fireSword", this));
            e.addCheck(new CheckValue("§bDiamond §fSword", "diamondSword", this));
            e.addCheck(new CheckValue("§eKnockback §fSword", "knockbackSword", this));
            e.addCheck(new CheckValue("§6Knockback §fRod", "knockbackRod", this));
            e.addCheck(new CheckValue("§4Strength §fPotion", "strengthPotion", this));
            e.addCheck(new CheckValue("§5Ender §fPearl", "enderPearl", this));
            e.addCheck(new CheckValue("§3Corrupt §fPearl", "corruptPearl", this));
            e.addCheck(new CheckValue("§dTime Warp §fPearl", "warpPearl", this));
        }));
        addExpand(new ExpandValue("Nametag Icon", e -> {
            e.addToggle(new ToggleValue("Show icon", "nametagIcon", this));
            e.addCheck(new CheckValue("§cSwords", "swordIcon", this));
            e.addCheck(new CheckValue("§6Knockback §fItems", "knockbackIcon", this));
            e.addCheck(new CheckValue("§5Ender §fPearl", "pearlIcon", this));
            e.addCheck(new CheckValue("§4Strength §fPotion", "strengthIcon", this));
        }));
    }

    public static boolean heldItem(UUID uuid, Item item) {
        Set<Item> set = HELD_ITEM_CACHE.get(uuid);
        return (set != null && set.contains(item));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Skywars.GAME.isNotActive()) return;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == null || player == this.mc.thePlayer ||
                    TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName()))
                continue;

            ItemStack held = player.getHeldItem();
            UUID uuid = player.getUniqueID();
            String itemName = null;
            if (held == null) continue;

            if (held.getItem() == Items.ender_pearl && held.isItemEnchanted() && held.getTooltip(this.mc.thePlayer, false).stream().anyMatch(s -> s.contains("Teleport back")) && this.warpPearl) {
                itemName = EnumChatFormatting.LIGHT_PURPLE + "Time Warp Pearl";
            } else if (held.getItem() == Items.ender_pearl && held.isItemEnchanted() && this.corruptPearl) {
                itemName = EnumChatFormatting.DARK_AQUA + "Corrupt Pearl";
            } else if (held.getItem() == Items.ender_pearl && this.enderPearl) {
                itemName = EnumChatFormatting.DARK_PURPLE + "Ender Pearl";
                markHeld(uuid, Items.ender_pearl);
            } else if (held.getItem() == Items.diamond_sword && this.diamondSword) {
                itemName = EnumChatFormatting.AQUA + "Diamond Sword";
                markHeld(uuid, Items.diamond_sword);
            } else if (held.getItem() == Items.iron_sword && held.isItemEnchanted() && this.fireSword) {
                itemName = EnumChatFormatting.RED + "Fire Sword";
                markHeld(uuid, Items.iron_sword);
            } else if (held.getItem() == Items.fishing_rod && held.isItemEnchanted() && held.getEnchantmentTagList().tagCount() == 1 && this.knockbackRod) {
                itemName = EnumChatFormatting.GOLD + "Knockback Rod";
                markHeld(uuid, Items.fishing_rod);
            } else if (held.getItem() == Items.stone_sword && held.isItemEnchanted() && this.knockbackSword) {
                itemName = EnumChatFormatting.YELLOW + "Knockback Sword";
                markHeld(uuid, Items.stone_sword);
            } else if (held.getItem() == Items.potionitem && held.getTooltip(this.mc.thePlayer, false).stream().anyMatch(s -> s.contains("Strength")) && this.strengthPotion) {
                itemName = EnumChatFormatting.DARK_RED + "Strength Potion";
                markHeld(uuid, Items.potionitem);
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
        String rawItemName = ColorUtil.unformattedText(itemName).toLowerCase();
        String distanceText = this.showDistance ? (EnumChatFormatting.GRAY + " (" + EnumChatFormatting.AQUA + distanceToEntity + "m" + EnumChatFormatting.GRAY + ")") : "";
        String text = NameUtil.getTabDisplayName(player.getName()) + EnumChatFormatting.GRAY + " has " + itemName;

        if (!this.alertType.equals("Notification")) {
            Meowtils.addMessage(text + distanceText);
        }

        if (!this.alertType.equals("Chat")) {
            NotificationManager.show("SkywarsAlerts", text, NotificationManager.Type.ALERT, 1500L);
        }

        if (this.soundMode.equals("All")) {
            sound();
        } else if ((this.soundMode.equals("Important") && rawItemName.equalsIgnoreCase("ender pearl")) ||
                rawItemName.equalsIgnoreCase("diamond sword") || rawItemName.equalsIgnoreCase("knockback rod") ||
                rawItemName.contains("strength")) {
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

    private static void markHeld(UUID uuid, Item item) {
        if (item == null) return;
        HELD_ITEM_CACHE.computeIfAbsent(uuid, k -> new HashSet<>()).add(item);
    }

    @Override
    public void onReset() {
        COOLDOWNS.clear();
        HELD_ITEM_CACHE.clear();
    }
}