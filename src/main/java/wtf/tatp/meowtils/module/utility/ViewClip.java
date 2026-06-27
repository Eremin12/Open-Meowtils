package wtf.tatp.meowtils.module.utility;

import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;

public class ViewClip extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;

    public ViewClip() {
        super("ViewClip", Module.Category.Utility);
        tag(Module.ModuleTag.SAFE);
        tooltip("Allows your view to clip through blocks when in third person.");
    }
}