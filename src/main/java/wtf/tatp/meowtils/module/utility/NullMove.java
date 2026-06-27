package wtf.tatp.meowtils.module.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;

public class NullMove extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;

    private static long LEFT;
    private static long RIGHT;
    private static long FORWARD;
    private static long BACK;

    public NullMove() {
        super("NullMove", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Does not prevent movement if several movement keys are held at once.");
    }

    public static boolean shouldOverride() {
        Minecraft mc = Minecraft.getMinecraft();
        NullMove nm = Module.get(NullMove.class);
        return (nm != null && nm.enabled && mc.currentScreen == null && mc.thePlayer != null && mc.theWorld != null);
    }

    public static boolean isMovementKey(int keyCode) {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        return (keyCode == gameSettings.keyBindRight.getKeyCode() || keyCode == gameSettings.keyBindLeft.getKeyCode() ||
                keyCode == gameSettings.keyBindForward.getKeyCode() || keyCode == gameSettings.keyBindBack.getKeyCode());
    }

    public static Boolean checkKey(int keyCode, boolean pressed) {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        if (!pressed) return false;

        if (keyCode == gameSettings.keyBindRight.getKeyCode()) {
            return (RIGHT == 0L || RIGHT <= LEFT);
        }

        if (keyCode == gameSettings.keyBindLeft.getKeyCode()) {
            return (LEFT == 0L || LEFT <= RIGHT);
        }

        if (keyCode == gameSettings.keyBindForward.getKeyCode()) {
            return (BACK == 0L || BACK <= FORWARD);
        }

        if (keyCode == gameSettings.keyBindBack.getKeyCode()) {
            return (FORWARD == 0L || FORWARD <= BACK);
        }
        return null;
    }

    public static void updateTime(int keyCode, boolean pressed) {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        long time = pressed ? System.nanoTime() : 0L;

        if (keyCode == gameSettings.keyBindRight.getKeyCode()) { LEFT = time; }
        else if (keyCode == gameSettings.keyBindLeft.getKeyCode()) { RIGHT = time; }
        else if (keyCode == gameSettings.keyBindForward.getKeyCode()) { FORWARD = time; }
        else if (keyCode == gameSettings.keyBindBack.getKeyCode()) { BACK = time; }
    }
}