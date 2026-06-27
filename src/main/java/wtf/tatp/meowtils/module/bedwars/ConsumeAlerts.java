package wtf.tatp.meowtils.module.bedwars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Duels;
import wtf.tatp.meowtils.module.hypixel.PartyNotifier;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;

public class ConsumeAlerts extends Module {
    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int distance = 0;
    @Config
    public boolean sound = true;
    @Config
    public boolean showDistance = true;
    @Config
    public boolean goldenApple = true;
    @Config
    public String alertType = "Chat";
    @Config
    public boolean milk = true;
    @Config
    public boolean speed = true;
    @Config
    public boolean jump = true;
    @Config
    public boolean invis = true;

    private static final Map<UUID, Long> LAST_ALERT_TIME = new HashMap<>();
    private static final Map<UUID, TrackedUse> USING_ITEM = new HashMap<>();
    private static final long ALERT_COOLDOWN = 1600L;
    private final Minecraft mc = Minecraft.getMinecraft();

    public ConsumeAlerts() {
        super("ConsumeAlerts", Module.Category.Bedwars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Alerts you when players consume a specific item.");
        addMode(new ModeValue("Alert", Arrays.asList("Chat", "Notification", "All"), "alertType", this));
        addSlider(new SliderValue("Max distance", 0.0D, 250.0D, 5.0D, "m", "distance", this, int.class));
        addToggle(new ToggleValue("Ping sound", "sound", this));
        addToggle(new ToggleValue("Show distance", "showDistance", this));
        addCheck(new CheckValue("§6Golden Apple ", "goldenApple", this));
        addCheck(new CheckValue("Milk", "milk", this));
        addCheck(new CheckValue("§eSpeed Potion", "speed", this));
        addCheck(new CheckValue("§aJump Potion", "jump", this));
        addCheck(new CheckValue("§bInvis Potion", "invis", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (event.getPhase() != ClientTickEvent.Phase.POST || mc.theWorld == null || mc.thePlayer == null)
            return;
        if (Bedwars.GAME.isNotActive() && Duels.BEDWARS.isNotActive())
            return;
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer || TeamUtil.isTeam(player) || TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName()))
                continue;
            float distanceToPlayer = player.getDistanceToEntity(mc.thePlayer);
            if (this.distance > 0 && distanceToPlayer > this.distance)
                continue;
            UUID uuid = player.getUniqueID();
            ItemStack heldItem = player.getHeldItem();
            boolean isUsing = player.isUsingItem();
            TrackedUse previous = USING_ITEM.get(uuid);
            if (isUsing && heldItem != null && isTrackedItem(heldItem.getItem())) {
                if (previous == null || !ItemStack.areItemStacksEqual(heldItem, previous.item))
                    USING_ITEM.put(uuid, new TrackedUse(heldItem, System.currentTimeMillis()));
                continue;
            }
            if (previous != null) {
                USING_ITEM.remove(uuid);
                if (heldItem == null || !ItemStack.areItemStacksEqual(heldItem, previous.item)) {
                    alert(player, previous.item);
                }
            }
        }
    }

    private static boolean isTrackedItem(Item item) {
        return item == Items.golden_apple || item == Items.potionitem || item == Items.milk_bucket;
    }

    private void alert(EntityPlayer player, ItemStack item) {
        UUID uuid = player.getUniqueID();
        long now = System.currentTimeMillis();
        Long lastAlert = LAST_ALERT_TIME.getOrDefault(uuid, 0L);
        if (now - lastAlert < 1600L)
            return;
        LAST_ALERT_TIME.put(uuid, now);
        String name = player.getName();
        int distanceToEntity = (int) player.getDistanceToEntity(mc.thePlayer);
        String distance = this.showDistance ? EnumChatFormatting.GRAY + " (" + EnumChatFormatting.AQUA + distanceToEntity + "m" + EnumChatFormatting.GRAY + ")" : "";
        Item heldItem = item.getItem();
        if (heldItem == Items.golden_apple && this.goldenApple) {
            send(name, "Golden Apple", distance);
        } else if (heldItem == Items.milk_bucket && this.milk) {
            send(name, "Milk", distance);
        } else if (heldItem == Items.potionitem) {
            String potion = item.getUnlocalizedName().toLowerCase();
            if (potion.contains("speed") && this.speed) {
                send(name, "Speed Potion", distance);
            } else if (potion.contains("jump") && this.jump) {
                send(name, "Jump Potion", distance);
            } else if (potion.contains("invis") && this.invis) {
                send(name, "Invis Potion", distance);
            }
        }
    }

    private void send(String name, String item, String distance) {
        EnumChatFormatting color;
        switch (item) {
            case "Golden Apple":
                color = EnumChatFormatting.GOLD;
                break;
            case "Milk":
                color = EnumChatFormatting.WHITE;
                break;
            case "Speed Potion":
                color = EnumChatFormatting.YELLOW;
                break;
            case "Jump Potion":
                color = EnumChatFormatting.GREEN;
                break;
            case "Invis Potion":
                color = EnumChatFormatting.AQUA;
                break;
            default:
                color = EnumChatFormatting.GRAY;
                break;
        }
        if (!this.alertType.equals("Notification")) {
            Meowtils.addMessage(NameUtil.getTabDisplayName(name) + EnumChatFormatting.GRAY + " consumed " + color + item + distance);
        }
        if (!this.alertType.equals("Chat")) {
            NotificationManager.show("ConsumeAlerts", NameUtil.getTabDisplayName(name) + EnumChatFormatting.GRAY + " consumed " + color + item, NotificationManager.Type.ALERT, 1500L);
        }
        PartyNotifier.consumeAlerts(name, item);
        if (this.sound)
            Util.playSound(Util.Sound.PING_MEDIUM, 100);
    }

    private static class TrackedUse {
        final ItemStack item;
        final long startUseDuration;

        TrackedUse(ItemStack item, long useDuration) {
            this.item = item;
            this.startUseDuration = useDuration;
        }
    }

    public void onReset() {
        USING_ITEM.clear();
        LAST_ALERT_TIME.clear();
    }
}