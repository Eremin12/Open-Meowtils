package wtf.tatp.meowtils.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import net.minecraft.client.Minecraft;

public class User32Util {

    private static User32 user32Instance;

    private static User32 getUser32() {
        Minecraft mc = Minecraft.getMinecraft();
        if (user32Instance == null && Platform.isWindows() && mc.thePlayer != null && mc.theWorld != null && mc.isFullScreen()) {
            try {
                user32Instance = (User32) Native.loadLibrary("user32", User32.class);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return user32Instance;
    }

    public static boolean holdingRightClick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isFullScreen() || mc.currentScreen != null || mc.theWorld == null || mc.thePlayer == null) return false;

        User32 u32 = getUser32();
        return (u32 != null && (u32.GetAsyncKeyState(2) & 0x8000) != 0);
    }

    public static boolean holdingLeftClick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isFullScreen() || mc.currentScreen != null || mc.theWorld == null || mc.thePlayer == null) return false;

        User32 u32 = getUser32();
        return (u32 != null && (u32.GetAsyncKeyState(1) & 0x8000) != 0);
    }

    public interface User32 extends Library {
        short GetAsyncKeyState(int key);
    }
}