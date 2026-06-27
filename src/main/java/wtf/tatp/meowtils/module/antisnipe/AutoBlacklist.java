package wtf.tatp.meowtils.module.antisnipe;

import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.lists.BlacklistManager;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.MojangNameToUUID;
import wtf.tatp.meowtils.util.NameUtil;

public class AutoBlacklist extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public boolean showNotifications = true;
    @Config
    public boolean forFlags = true;
    @Config
    public boolean flagAutoblock = true;
    @Config
    public boolean flagNoslow = true;
    @Config
    public boolean flagKillaura = true;
    @Config
    public boolean flagLegitScaffold = true;
    @Config
    public boolean forReports = true;
    @Config
    public boolean whenReportCommand = true;
    @Config
    public boolean whenWdrCommand = true;

    public AutoBlacklist() {
        super("AutoBlacklist", Module.Category.Antisnipe);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Automatically adds players to the blacklist for selected events.\n§cNOTE: Some features require AntiCheat module to be enabled.");
        addToggle(new ToggleValue("Show notifications", "showNotifications", this));
        addExpand(new ExpandValue("For flags", e -> {
            e.addToggle(new ToggleValue("Enabled", "forFlags", this));
            e.addCheck(new CheckValue("When flags §cAutoBlock", "flagAutoblock", this));
            e.addCheck(new CheckValue("When flags §cNoSlow", "flagNoslow", this));
            e.addCheck(new CheckValue("When flags §cKillaura", "flagKillaura", this));
            e.addCheck(new CheckValue("When flags §cLegit Scaffold", "flagLegitScaffold", this));
        }));
        addExpand(new ExpandValue("For reports", e -> {
            e.addToggle(new ToggleValue("Enabled", "forReports", this));
            e.addCheck(new CheckValue("When §b/report", "whenReportCommand", this));
            e.addCheck(new CheckValue("When §b/wdr", "whenWdrCommand", this));
        }));
    }

    public static void blacklistPlayer(String player, String reasons) {
        MojangNameToUUID.lookup(player, uuid -> {
            if (uuid != null) {
                if (!BlacklistManager.isBlacklisted(uuid)) {
                    sendNotification(player);
                }
                BlacklistManager.appendReason(uuid, reasons);
            } else {
                if (!BlacklistManager.isBlacklisted(player)) {
                    sendNotification(player);
                }
                BlacklistManager.appendReason(player, reasons);
            }
        });
    }

    private static void sendNotification(String player) {
        AutoBlacklist autoBlacklist = Module.get(AutoBlacklist.class);
        if (autoBlacklist != null && autoBlacklist.showNotifications) {
            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                Meowtils.addMessage(EnumChatFormatting.RED + "Auto-blacklisted " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(player) + EnumChatFormatting.RED + ".");
            }

            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                NotificationManager.show("AutoBlacklist", NameUtil.getTabDisplayName(player), NotificationManager.Type.ALERT, 1000L);
            }
        }
    }
}