package wtf.tatp.meowtils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.meowtils.GUI;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.Prefix;

public class MeowtilsAlert {

    private static int tickCounter = 0;

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        GUI g = Module.get(GUI.class);

        if (mc.thePlayer == null || mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) {
            return;
        }

        tickCounter++;
        if (tickCounter < 60) {
            return;
        }

        Notifications notifications = Module.get(Notifications.class);
        if (notifications != null && notifications.startNotifications && (g == null || !g.firstStartup)) {
            documentationMessage();
        }

        if (g != null && g.firstStartup) {
            Meowtils.addMessage(EnumChatFormatting.BLUE + "Welcome to " + EnumChatFormatting.DARK_PURPLE.toString() + EnumChatFormatting.BOLD + "Meowtils" + EnumChatFormatting.BLUE + "!");
            Meowtils.addMessage(EnumChatFormatting.RED + "Open GUI: " + EnumChatFormatting.GREEN + "Right Shift");
            Meowtils.addMessage(EnumChatFormatting.GRAY + "If you are unable to open the GUI, you can use the " + EnumChatFormatting.GREEN + "/meowtilsgui" + EnumChatFormatting.GRAY + " command and rebind in the " + EnumChatFormatting.BLUE + "GUI Module" + EnumChatFormatting.GRAY + " or use the " + EnumChatFormatting.GREEN + "/bind <key>" + EnumChatFormatting.GRAY + " command to set it directly.");

            documentationMessage();

            g.firstStartup = false;
            ConfigManager.save();
        }

        EventManager.unregister(this);
    }

    private static void documentationMessage() {
        ChatComponentText message = new ChatComponentText(Prefix.getPrefix() + "Read the full documentation at " + EnumChatFormatting.LIGHT_PURPLE.toString() + EnumChatFormatting.UNDERLINE + "docs.tatp.wtf" + EnumChatFormatting.WHITE + ".");

        message.setChatStyle(message.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://docs.tatp.wtf/"))
                .setUnderlined(true));

        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    }
}