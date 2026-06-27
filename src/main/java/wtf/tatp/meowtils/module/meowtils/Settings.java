package wtf.tatp.meowtils.module.meowtils;

import java.util.Arrays;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public class Settings extends Module {

    @Config
    public boolean enabled = true;
    @Config
    public int key = 0;
    @Config
    public String themeM = "WHITE";
    @Config
    public String themeE = "WHITE";
    @Config
    public String themeO = "WHITE";
    @Config
    public String themeW = "WHITE";
    @Config
    public String themeFirstBracket = "GRAY";
    @Config
    public String themeSecondBracket = "GRAY";
    @Config
    public boolean autoUpdate = true;
    @Config
    public boolean smoothFont = true;
    @Config
    public String prefix = "Default";
    @Config
    public boolean lowerCase = false;
    @Config
    public boolean copyChat = true;

    public Settings() {
        super("Settings", Module.Category.Meowtils, true);
        tooltip("General Meowtils settings.\n§bAuto-Updates §f- Automatically download new updates\n§bSmooth font §f- Toggle smooth font for HUD elements\n§bCopy chat §f- Copy hovered chat message on right click\n§bPrefix §f- Change chat prefix colors\n§d/theme §f- Set custom prefix colors");
        addToggle(new ToggleValue("Auto-Updates", "autoUpdate", this));
        addToggle(new ToggleValue("Smooth font", "smoothFont", this));
        addToggle(new ToggleValue("Copy chat", "copyChat", this));
        addMode(new ModeValue("Prefix", Arrays.asList("Default", "Myau", "Fire", "Nebula", "Air", "Custom", "Short"), "prefix", this));
        addCheck(new CheckValue("Lowercase", "lowerCase", this));
        addButton(new ButtonValue("Preview", 5.0F, () -> Meowtils.addMessage(this.prefix)));
    }
}