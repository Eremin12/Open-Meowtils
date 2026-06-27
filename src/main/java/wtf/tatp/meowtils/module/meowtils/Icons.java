package wtf.tatp.meowtils.module.meowtils;

import java.util.Arrays;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ModeValue;

public class Icons extends Module {

    @Config
    public boolean enabled = true;
    @Config
    public int key = 0;
    @Config
    public String display = "Always";
    @Config
    public boolean blacklistIcon = true;
    @Config
    public boolean safelistIcon = true;
    @Config
    public boolean friendIcon = true;

    public Icons() {
        super("Icons", Module.Category.Meowtils, true);
        tooltip("Settings for player icons.");
        addMode(new ModeValue("Display", Arrays.asList("Always", "Tablist", "Nametags"), "display", this));
        addCheck(new CheckValue("§4Blacklist §fIcons", "blacklistIcon", this));
        addCheck(new CheckValue("§aSafelist §fIcons", "safelistIcon", this));
        addCheck(new CheckValue("§6Friend §fIcons", "friendIcon", this));
    }

    public static boolean displayInTab() {
        Icons i = Module.get(Icons.class);
        if (i == null) return true;
        return (i.display.equals("Tablist") || i.display.equals("Always"));
    }

    public static boolean displayInNametag() {
        Icons i = Module.get(Icons.class);
        if (i == null) return true;
        return (i.display.equals("Nametags") || i.display.equals("Always"));
    }
}