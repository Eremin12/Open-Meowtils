package wtf.tatp.meowtils.extension;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.ModuleManager;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public abstract class Extension extends Module {

    private final String author;
    public final Object owner = this;
    private final String moduleName;

    protected Extension(String moduleName, @Nullable String author) {
        super(moduleName, Module.Category.Extensions);
        tag(Module.ModuleTag.LEGIT);
        this.author = author;
        this.moduleName = moduleName;
    }

    public Object getOwner() {
        return this;
    }

    public void toggle(String name, String config) {
        try {
            addToggle(new ToggleValue(name, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add toggle component");
            t.printStackTrace();
        }
    }

    public void check(String name, String config) {
        try {
            addCheck(new CheckValue(name, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add check component");
            t.printStackTrace();
        }
    }

    public void slider(String name, double min, double max, double increment, @Nullable String valueType, String config, Class<?> targetType) {
        try {
            addSlider(new SliderValue(name, min, max, increment, valueType, config, this, targetType));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add slider component");
            t.printStackTrace();
        }
    }

    public void mode(String name, List<String> modes, String config) {
        try {
            addMode(new ModeValue(name, modes, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add mode component");
            t.printStackTrace();
        }
    }

    public void text(@Nullable String name, String config) {
        try {
            addText(new TextValue(name, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add text component");
            t.printStackTrace();
        }
    }

    public void text(@Nullable String name, String description, String config) {
        try {
            addText(new TextValue(name, description, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add text component");
            t.printStackTrace();
        }
    }

    public void color(String name, @Nonnull ColorLink link) {
        try {
            addColor(new ColorValue(name, link));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add color component");
            t.printStackTrace();
        }
    }

    public void saturation(@Nonnull ColorLink link) {
        try {
            addSaturation(new SaturationValue(link));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add saturation component");
            t.printStackTrace();
        }
    }

    public void brightness(@Nonnull ColorLink link) {
        try {
            addBrightness(new BrightnessValue(link));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add brightness component");
            t.printStackTrace();
        }
    }

    public void opacity(String name, String config) {
        try {
            addOpacity(new OpacityValue(name, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add opacity component");
            t.printStackTrace();
        }
    }

    @Deprecated
    public void opacity(String name, String config, ColorLink link) {
        try {
            addOpacity(new OpacityValue(name, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add opacity component");
            t.printStackTrace();
        }
    }

    public void info(String name) {
        try {
            if (this.author != null) {
                tooltip(name + "\n§9Author: §9" + this.author);
            } else {
                tooltip(name);
            }
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add info component");
            t.printStackTrace();
        }
    }

    public void button(String name, float textScale, Runnable action) {
        try {
            addButton(new ButtonValue(name, textScale, action));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add button component");
            t.printStackTrace();
        }
    }

    public void bind(String name, String config) {
        try {
            addBind(new BindValue(name, config, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add bind component");
            t.printStackTrace();
        }
    }

    public void expand(String name, Consumer<ExpandValue> builder) {
        try {
            addExpand(new ExpandValue(name, builder, this));
        } catch (Throwable t) {
            Meowtils.error(this.moduleName + " failed to add expand component");
            t.printStackTrace();
        }
    }

    public ColorLink linkColor(String redConfig, String greenConfig, String blueConfig) {
        return new ColorLink(redConfig, greenConfig, blueConfig, this);
    }

    public static void registerModule(Module module) {
        if (module == null) {
            throw new IllegalArgumentException("Module cannot be null");
        }
        if (ModuleManager.getModules().contains(module)) {
            throw new IllegalStateException("Duplicate module name: " + module);
        }
        ModuleManager.register(module);
        ExtensionManager.EXTENSION_MODULES.add(module);
    }

    public static void registerEvent(Object listener) {
        try {
            EventManager.register(listener);
            ExtensionManager.EXTENSION_LISTENERS.add(listener);
        } catch (Throwable t) {
            Meowtils.error("Unable to register event " + listener);
            t.printStackTrace();
        }
    }
}