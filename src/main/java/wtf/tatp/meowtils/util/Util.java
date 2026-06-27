package wtf.tatp.meowtils.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.manager.SoundLoader;
import wtf.tatp.meowtils.module.meowtils.Notifications;

public class Util {

    public enum Sound {
        PING,
        PING_DEEP,
        PING_MEDIUM,
        LEVEL,
        ANVIL,
        MEOW,
        ANVIL_BREAK,
        ERROR,
        ERROR_DEEP,
        CRIT;
    }

    public static void playSound(Sound sound, int volume) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || sound == null) return;

        float vol = volume / 100.0F;

        if (sound == Sound.PING) {
            mc.thePlayer.playSound("random.orb", vol, 1.0F);
        }

        if (sound == Sound.PING_DEEP) {
            mc.thePlayer.playSound("random.orb", vol, 0.2F);
        }

        if (sound == Sound.PING_MEDIUM) {
            mc.thePlayer.playSound("random.orb", vol, 0.5F);
        }

        if (sound == Sound.LEVEL) {
            mc.thePlayer.playSound("random.levelup", vol, 2.0F);
        }

        if (sound == Sound.ANVIL) {
            mc.thePlayer.playSound("random.anvil_land", vol, 1.8F);
        }

        if (sound == Sound.MEOW) {
            mc.thePlayer.playSound("mob.cat.meow", vol, 1.0F);
        }

        if (sound == Sound.ANVIL_BREAK) {
            mc.thePlayer.playSound("random.anvil_use", vol, 1.0F);
        }

        if (sound == Sound.ERROR) {
            mc.thePlayer.playSound("note.bass", vol, 1.0F);
        }

        if (sound == Sound.ERROR_DEEP) {
            mc.thePlayer.playSound("note.bass", vol, 0.6F);
        }

        if (sound == Sound.CRIT) {
            SoundLoader.playSound(volume);
        }
    }

    public static String formatTimestamp(long ms) {
        Date date = new Date(ms);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    public static String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return m + "m " + s + "s";
    }

    public static void openFolder(File dir, String id) {
        try {
            if (Desktop.isDesktopSupported()) {
                if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                    Meowtils.addMessage("Opening " + id + " folder.");
                }
                if (Notifications.getMode() != Notifications.Mode.CHAT) {
                    NotificationManager.show("Opening folder", id, NotificationManager.Type.INFO, 2000L);
                }
                Desktop.getDesktop().open(dir);
            } else {
                Meowtils.addMessage(EnumChatFormatting.RED + "Failed to open " + id + " folder on this system!");
                Meowtils.addMessage(EnumChatFormatting.GRAY + "Open it manually: " + EnumChatFormatting.YELLOW + "<minecraft instance>/meowtils");
            }
        } catch (IOException e) {
            Meowtils.addMessage(EnumChatFormatting.RED + "Failed to open " + id + " folder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isClassLoaded(String path) {
        try {
            Class.forName(path);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static int parseIntFromString(String string, int start) {
        int result = 0;
        for (int i = start; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') break;
            result = result * 10 + c - '0';
        }
        return result;
    }

    public static void leftClick() {
        KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode());
    }

    public static void rightClick() {
        KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
    }
}