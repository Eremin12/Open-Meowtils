package wtf.tatp.meowtils.module.render;

import java.util.Arrays;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.Util;

public class Cape extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public String selectedCape = "2011";
    @Config
    public String customCapeName = "";
    @Config
    public boolean renderOnAll = false;

    public Cape() {
        super("Cape", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Renders a cape on players. You may import your own cape file.\n§d/capefolder §f- Open cape folder\n§bText §f- Input the custom cape file name");
        addMode(new ModeValue("Cape", Arrays.asList("2011", "2012", "2013", "2015", "2016", "Experience", "Founder", "Cobalt", "Astolfo", "Moon", "Myau", "Raven", "Custom"), "selectedCape", this));
        addText(new TextValue("Cape", "File name", "customCapeName", this));
        addToggle(new ToggleValue("Render for all", "renderOnAll", this));
        addButton(new ButtonValue("Cape folder", 5.0F, () -> Util.openFolder(Meowtils.CUSTOM_CAPE_DIR, "cape")));
    }
}