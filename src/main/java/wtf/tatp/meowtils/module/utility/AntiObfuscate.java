package wtf.tatp.meowtils.module.utility;

import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderStringEvent;
import wtf.tatp.meowtils.event.api.EventPriority;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;

public class AntiObfuscate extends Module {

    @Config
    public int key = 0;

    @Config
    public boolean enabled = false;

    public AntiObfuscate() {
        super("AntiObfuscate", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Deobfuscates text.");
    }

    @EventTarget(priority = EventPriority.LOWEST)
    public void onRenderString(RenderStringEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getString() == null) return;

        event.setString(event.getString().replace("§k", ""));
    }
}