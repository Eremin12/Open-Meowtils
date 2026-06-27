package wtf.tatp.meowtils.module.antisnipe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.lists.SafelistManager;
import wtf.tatp.meowtils.manager.session.Bedwars;
import wtf.tatp.meowtils.manager.session.Server;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.MojangNameToUUID;
import wtf.tatp.meowtils.util.NameUtil;

public class AutoSafelist extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean autoSafelistMessage = true;

    public AutoSafelist() {
        super("AutoSafelist", Module.Category.Antisnipe);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Automatically safelists players that take a final death.");
        addToggle(new ToggleValue("Safelist feedback", "autoSafelistMessage", this));
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        if (Bedwars.GAME.isNotActive()) return;

        String msg = event.getComponent().getUnformattedText();

        if (msg.contains("FINAL KILL!")) {
            Matcher matcher = Pattern.compile("^([A-Za-z0-9_]+)(?=[\\s'])").matcher(msg);

            if (matcher.find()) {
                String player = matcher.group(1);
                if (player.equals(this.mc.thePlayer.getName())) return;
                if (Server.HYPIXEL_REPLAY.isActive()) return;

                MojangNameToUUID.lookup(player, uuid -> {
                    if (SafelistManager.isSafelisted(uuid) || SafelistManager.isSafelisted(player)) return;

                    if (uuid != null) {
                        SafelistManager.add(uuid);
                    } else {
                        SafelistManager.add(player);
                    }

                    if (this.autoSafelistMessage) {
                        if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                            Meowtils.addMessage(EnumChatFormatting.GREEN + "Auto-safelisted " + EnumChatFormatting.RESET + NameUtil.getTabDisplayName(player) + EnumChatFormatting.GREEN + ".");
                        }
                        if (Notifications.getMode() != Notifications.Mode.CHAT) {
                            NotificationManager.show("AutoSafelist", NameUtil.getTabDisplayName(player), NotificationManager.Type.INFO, 1000L);
                        }
                    }
                });
            }
        }
    }
}