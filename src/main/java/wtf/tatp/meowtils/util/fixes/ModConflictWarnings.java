package wtf.tatp.meowtils.util.fixes;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.WorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.Util;

public class ModConflictWarnings {

    private static int tickCounter = 0;
    private static boolean checkedMods = false;
    private static boolean checkedProxy = false;

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        Notifications notifications = Module.get(Notifications.class);
        if (notifications == null || !notifications.modWarnings) return;

        if (!checkedMods) {
            tickCounter++;
            if (tickCounter < 20) {
                return;
            }

            if (Util.isClassLoaded("utils.extort.in.Main")) {
                alert("Utils", "Meowtils already contains all features from this mod. You should remove it.");
            }

            if (Util.isClassLoaded("fr.alexdoru.mwe.MWE")) {
                alert("MWE", "May cause some incompatibility issues as Meowtils contains certain similar features. Use with caution.");
            }

            if (Util.isClassLoaded("org.afterlike.openutils.OpenUtils")) {
                alert("OpenUtils", "Meowtils already contains most features from this mod, remove this if you notice issues.");
            }

            if (Util.isClassLoaded("utilsmod.BetterUtils")) {
                alert("BetterUtils", "Meowtils already contains most features of this mod. You should remove it.");
            }

            if (Util.isClassLoaded("net.labymod.core.LabyMod")) {
                alert("LabyMod", "Certain Meowtils features may not work on LabyMod. This is not a bug.");
            }

            if (Util.isClassLoaded("org.afterlike.lucid.platform.mixin.client.MinecraftMixin")) {
                alert("Lucid", "Meowtils already contains anticheat features, remove this if you notice issues.");
            }

            if (Util.isClassLoaded("club.sk1er.patcher.Patcher")) {
                alert("Patcher", "HUD Caching might cause HUD animations to not look as smooth.");
            }

            if (Util.isClassLoaded("xyz.blowsy.freelook.Main")) {
                alert("Freelook (TimeChanger)", "Meowtils already has a Freelook module.");
            }

            if (Meowtils.isLunar()) {
                alert("Lunar Client", "HUD Caching might cause HUD animations to not look as smooth.");
            }

            if (Util.isClassLoaded("org.polyfrost.chatting.mixin.ChatLineMixin")) {
                alert("Chatting", "Copy chat may not be compatible with this mod.");
            }

            if (Util.isClassLoaded("org.polyfrost.hytils.HytilsReborn")) {
                alert("Hytils", "Certain features conflict with this mod.");
            }

            checkedMods = true;
        }

        if (!checkedProxy) {
            if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.contains("localhost")) {
                Meowtils.addMessage(EnumChatFormatting.RED + "You appear to be using a local proxy, if this is for stats or utils, " + EnumChatFormatting.DARK_PURPLE + "Meowtils" + EnumChatFormatting.RED + " already has all of these features.");
            }

            checkedProxy = true;
        }
    }

    @EventTarget
    public void onWorldLoad(WorldEvent event) {
        if (event.getType() != WorldEvent.Type.LOAD) return;
        if (!event.getWorld().isRemote) return;
        checkedProxy = false;
    }

    private static void alert(String mod, String desc) {
        Meowtils.addMessage(EnumChatFormatting.RED + "You are currently using " + EnumChatFormatting.WHITE + mod + EnumChatFormatting.RED + ". It may not work correctly with " + EnumChatFormatting.DARK_PURPLE + "Meowtils" + EnumChatFormatting.RED + ". " + EnumChatFormatting.GRAY + "You can disable this in Notifications.");

        if (desc != null) {
            Meowtils.addMessage(EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC + desc);
        }
    }
}