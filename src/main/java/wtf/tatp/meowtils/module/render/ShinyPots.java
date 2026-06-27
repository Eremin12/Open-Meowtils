package wtf.tatp.meowtils.module.render;

import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;

public class ShinyPots extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;

    public ShinyPots() {
        super("ShinyPots", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Renders potion color as slot background.");
    }
}