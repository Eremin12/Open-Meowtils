package wtf.tatp.meowtils.module.meowtils;

import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.EntityJoinWorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.lists.BlacklistManager;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.util.DelayedTask;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.Util;

public class Notifications extends Module {

    @Config
    public boolean enabled = true;
    @Config
    public int key = 0;
    @Config
    public boolean toggle = true;
    @Config
    public boolean challengeWarning = true;
    @Config
    public boolean shoutCooldown = true;
    @Config
    public boolean modWarnings = true;
    @Config
    public boolean startNotifications = true;
    @Config
    public String notificationMode = "Chat";
    @Config
    public boolean blacklistedWarning = true;
    @Config
    public boolean showBannedPlayer = true;

    private static final HashSet<String> BLACKLISTED_ALERTED = new HashSet<>();
    private static final HashSet<String> PLAYERS_BEFORE = new HashSet<>();
    private static final HashSet<String> PLAYERS_AFTER = new HashSet<>();
    private static int tickCounter = 0;
    private static boolean shoutAlerted = true;
    public static long lastShout = 0L;
    public static int shoutTimeLeft = 0;

    public Notifications() {
        super("Notifications", Module.Category.Meowtils, true);
        tooltip("Select which notifications to send in chat.\n§bPrefer §f- Select whether to prefer notifications, chat messages, or both.");
        addMode(new ModeValue("Prefer", Arrays.asList("Notification", "Chat", "Both"), "notificationMode", this));
        addToggle(new ToggleValue("Module toggled", "toggle", this));
        addToggle(new ToggleValue("Start notifications", "startNotifications", this));
        addToggle(new ToggleValue("Mod conflict warnings", "modWarnings", this));
        addToggle(new ToggleValue("Challenge warning", "challengeWarning", this));
        addToggle(new ToggleValue("Shout cooldown", "shoutCooldown", this));
        addToggle(new ToggleValue("Blacklisted warning", "blacklistedWarning", this));
        addToggle(new ToggleValue("Show banned player", "showBannedPlayer", this));
    }

    public enum Mode {
        NOTIFICATION,
        CHAT,
        BOTH;
    }

    public static Mode getMode() {
        Notifications n = Module.get(Notifications.class);
        if (n == null) return Mode.BOTH;
        switch (n.notificationMode) {
            case "Notification":
                return Mode.NOTIFICATION;
            case "Chat":
                return Mode.CHAT;
        }
        return Mode.BOTH;
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        String msg = event.getComponent().getUnformattedText();

        // Shout cooldown tracking
        if (this.shoutCooldown && msg.startsWith("[SHOUT]") && msg.contains(this.mc.thePlayer.getName() + ":") && Server.HYPIXEL.isActive()) {
            lastShout = System.currentTimeMillis();
            shoutTimeLeft = 60;
            shoutAlerted = false;
        }

        // Challenge warning
        if (this.challengeWarning && msg.contains("You can disable Challenges through any NPC in the Bed Wars lobby using the redstone on the Challenges page.") && !msg.contains(":")) {
            new DelayedTask(() -> {
                if (getMode() != Mode.NOTIFICATION) {
                    Meowtils.addMessage(EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + "You currently have a " + EnumChatFormatting.DARK_AQUA.toString() + EnumChatFormatting.BOLD + "bedwars challenge" + EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD + " active!");
                }
                if (getMode() != Mode.CHAT) {
                    NotificationManager.show("Warning", "Challenge active!", NotificationManager.Type.WARNING, 2000L);
                }
                Util.playSound(Util.Sound.MEOW, 100);
            }, 40);
        }

        // Show banned player
        if (this.showBannedPlayer && msg.contains("A player has been removed from your game.")) {
            PLAYERS_BEFORE.clear();
            for (EntityPlayer player : this.mc.theWorld.playerEntities) {
                if (player == null) continue;
                PLAYERS_BEFORE.add(player.getName());
            }

            new DelayedTask(() -> {
                PLAYERS_AFTER.clear();
                for (EntityPlayer player : this.mc.theWorld.playerEntities) {
                    if (player == null) continue;
                    PLAYERS_AFTER.add(player.getName());
                }
                PLAYERS_BEFORE.removeAll(PLAYERS_AFTER);
                if (PLAYERS_BEFORE.isEmpty()) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "No players removed!");
                    return;
                }
                for (String name : PLAYERS_BEFORE) {
                    if (name == null || name.isEmpty()) continue;
                    Meowtils.addMessage(EnumChatFormatting.RED + "Player removed: " + EnumChatFormatting.GRAY + name);
                }
            }, 20);
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (Server.HYPIXEL.isNotActive()) return;

        if (this.shoutCooldown) {
            if (shoutTimeLeft > 0) {
                tickCounter++;
                if (tickCounter < 20) return;
                tickCounter = 0;
                shoutTimeLeft--;
            }

            if (!shoutAlerted && shoutTimeLeft <= 0) {
                shoutAlerted = true;

                if (getMode() != Mode.NOTIFICATION) {
                    Meowtils.addMessage(EnumChatFormatting.BOLD + "You may now " + EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "shout" + EnumChatFormatting.WHITE.toString() + EnumChatFormatting.BOLD + " again!");
                }

                if (getMode() != Mode.CHAT) {
                    NotificationManager.show("Shout", "Cooldown ended.", NotificationManager.Type.INFO, 1500L);
                }
            }
        }
    }

    @EventTarget
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!this.blacklistedWarning) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        if (event.getEntity() == this.mc.thePlayer) return;

        String uuid = event.getEntity().getUniqueID().toString();

        if (BLACKLISTED_ALERTED.contains(uuid)) return;

        if (BlacklistManager.isBlacklisted(uuid)) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Warning: " + EnumChatFormatting.RESET +
                    NameUtil.getTabDisplayName(event.getEntity().getName()) + EnumChatFormatting.GRAY + " is blacklisted since: " +
                    BlacklistManager.getFormattedEntry(uuid));

            if (getMode() != Mode.CHAT) {
                NotificationManager.show("Blacklisted Player", event.getEntity().getName(), NotificationManager.Type.WARNING, 2000L);
            }

            BLACKLISTED_ALERTED.add(uuid);
        }
    }

    @Override
    public void onReset() {
        BLACKLISTED_ALERTED.clear();
        PLAYERS_BEFORE.clear();
        PLAYERS_AFTER.clear();
        tickCounter = 0;
        shoutAlerted = true;
        lastShout = 0L;
        shoutTimeLeft = 0;
    }
}