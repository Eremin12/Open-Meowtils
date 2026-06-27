package wtf.tatp.meowtils.module.antisnipe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.module.hypixel.PartyNotifier;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.NameUtil;
import wtf.tatp.meowtils.util.Prefix;
import wtf.tatp.meowtils.util.TeamUtil;
import wtf.tatp.meowtils.util.Util;
import wtf.tatp.meowtils.util.anticheat.AntiCheatData;

public class AntiCheat extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int violationLevel = 0;
    @Config
    public boolean flagSound = true;
    @Config
    public boolean wdrButton = true;
    @Config
    public boolean autoBlock = true;
    @Config
    public boolean noSlow = true;
    @Config
    public boolean killaura = true;
    @Config
    public boolean legitScaffold = true;
    @Config
    public String componentColor = "RED";
    @Config
    public String buttonColor = "AQUA";
    @Config
    public String bracketColor = "GRAY";

    private static final Map<UUID, AntiCheatData> ANTICHEAT_DATA = new HashMap<>();
    private static final Map<String, Map<String, Integer>> VIOLATION_LEVELS = new HashMap<>();

    public AntiCheat() {
        super("AntiCheat", Module.Category.Antisnipe);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Detects suspicious behaviour of players around you. May not be 100% accurate.\n§d/anticheat §f- Set anticheat message colors");
        addSlider(new SliderValue("Violation level", 0.0D, 10.0D, 1.0D, null, "violationLevel", this, int.class));
        addToggle(new ToggleValue("Flag sound", "flagSound", this));
        addToggle(new ToggleValue("WDR Button", "wdrButton", this));
        addCheck(new CheckValue("AutoBlock", "autoBlock", this));
        addCheck(new CheckValue("NoSlow", "noSlow", this));
        addCheck(new CheckValue("Killaura", "killaura", this));
        addCheck(new CheckValue("Legit Scaffold", "legitScaffold", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        AutoBlacklist a = Module.get(AutoBlacklist.class);

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == this.mc.thePlayer || player.getName() == null ||
                    TeamUtil.isTeam(player) ||
                    TeamUtil.ignoreFriends(player.getUniqueID().toString()) || TeamUtil.ignoreFriends(player.getName()))
                continue;

            AntiCheatData data = ANTICHEAT_DATA.computeIfAbsent(player.getUniqueID(), k -> new AntiCheatData());
            data.anticheatCheck(player);

            String playerName = player.getName();

            if (data.failedAutoBlock() && incrementViolation(playerName, "AutoBlock")) {
                sendFlagMessage(playerName, "AutoBlock");
                data.autoBlockCheck.reset();
                blacklist(playerName, "autoblock", a != null && a.flagAutoblock);
            }
            if (data.failedNoSlow() && incrementViolation(playerName, "NoSlow")) {
                sendFlagMessage(playerName, "NoSlow");
                data.noSlowCheck.reset();
                blacklist(playerName, "noslow", a != null && a.flagNoslow);
            }
            if (data.failedLegitScaffold() && incrementViolation(playerName, "Legit Scaffold")) {
                sendFlagMessage(playerName, "Legit Scaffold");
                data.legitScaffoldCheck.reset(player.getUniqueID());
                blacklist(playerName, "legit scaffold", a != null && a.flagLegitScaffold);
            }
            if (data.failedKillauraB() && incrementViolation(playerName, "KillAura")) {
                sendFlagMessage(playerName, "Killaura");
                data.killauraCheck.reset();
                blacklist(playerName, "killaura", a != null && a.flagKillaura);
            }
        }
    }

    private boolean incrementViolation(String playerName, String checkType) {
        Map<String, Integer> playerViolations = VIOLATION_LEVELS.computeIfAbsent(playerName, k -> new HashMap<>());
        int newLevel = playerViolations.getOrDefault(checkType, 0) + 1;
        playerViolations.put(checkType, newLevel);

        if (newLevel >= this.violationLevel) {
            playerViolations.put(checkType, 0);
            return true;
        }
        return false;
    }

    private static void blacklist(String playerName, String reason, boolean shouldBlacklist) {
        AutoBlacklist a = Module.get(AutoBlacklist.class);
        if (a == null || !a.enabled) return;
        if (!a.forFlags) return;
        if (!shouldBlacklist) return;
        if (Server.HYPIXEL_REPLAY.isActive()) return;

        AutoBlacklist.blacklistPlayer(playerName, reason);
    }

    private void sendFlagMessage(String playerName, String checkType) {
        String msg = NameUtil.getTabDisplayName(playerName) + EnumChatFormatting.GRAY + " failed " + ColorUtil.getColorFromString(this.componentColor) + checkType;

        if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
            if (this.wdrButton) {
                ChatComponentText message = new ChatComponentText(Prefix.getPrefix() + msg + " ");

                ChatComponentText wdrButton = new ChatComponentText(ColorUtil.getColorFromString(this.bracketColor) + "[" + ColorUtil.getColorFromString(this.buttonColor) + "WDR" + ColorUtil.getColorFromString(this.bracketColor) + "]");
                wdrButton.setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wdr " + playerName))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Click to report this player."))));
                message.appendSibling(wdrButton);
                this.mc.thePlayer.addChatMessage(message);
            } else {
                Meowtils.addMessage(msg);
            }
        }

        if (Notifications.getMode() != Notifications.Mode.CHAT) {
            NotificationManager.show("AntiCheat", msg, NotificationManager.Type.WARNING, 1500L);
        }

        if (this.flagSound) {
            Util.playSound(Util.Sound.PING, 100);
        }

        PartyNotifier.antiCheat(playerName, checkType);
    }

    @Override
    public void onReset() {
        ANTICHEAT_DATA.clear();
        VIOLATION_LEVELS.clear();
    }
}