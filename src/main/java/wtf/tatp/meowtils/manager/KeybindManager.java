package wtf.tatp.meowtils.manager;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.ModuleManager;

public class KeybindManager {

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (mc.currentScreen != null) return;

        for (Module module : ModuleManager.getModules()) {
            int key = module.getKey();
            if (key <= 0) continue;

            boolean down = Keyboard.isKeyDown(key);

            if (down && !module.isKeyHeld()) {
                module.setState(!module.getState());
                module.setKeyHeld(true);
            }

            if (!down) {
                module.setKeyHeld(false);
            }
        }
    }
}