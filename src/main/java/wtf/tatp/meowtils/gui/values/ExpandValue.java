package wtf.tatp.meowtils.gui.values;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.ExpandComponent;

public class ExpandValue {

    private final String name;
    private boolean expanded;
    private final List<Object> subValues = new ArrayList<>();
    private Object owner;

    public ExpandValue(String name, Consumer<ExpandValue> builder) {
        this.name = name;
        builder.accept(this);
    }

    public ExpandValue(String name, Consumer<ExpandValue> builder, Object owner) {
        this.name = name;
        this.owner = owner;
        builder.accept(this);
    }

    public void addBind(BindValue v) {
        this.subValues.add(v);
    }

    public void addBrightness(BrightnessValue v) {
        this.subValues.add(v);
    }

    public void addButton(ButtonValue v) {
        this.subValues.add(v);
    }

    public void addCheck(CheckValue v) {
        this.subValues.add(v);
    }

    public void addColor(ColorValue v) {
        this.subValues.add(v);
    }

    public void addMode(ModeValue v) {
        this.subValues.add(v);
    }

    public void addOpacity(OpacityValue v) {
        this.subValues.add(v);
    }

    public void addSaturation(SaturationValue v) {
        this.subValues.add(v);
    }

    public void addSlider(SliderValue v) {
        this.subValues.add(v);
    }

    public void addText(TextValue v) {
        this.subValues.add(v);
    }

    public void addToggle(ToggleValue v) {
        this.subValues.add(v);
    }

    public List<Object> getSubValues() {
        return this.subValues;
    }

    public String getName() {
        return this.name;
    }

    public boolean getState() {
        return this.expanded;
    }

    public void setState(boolean state) {
        if (state == getState()) return;
        this.expanded = state;
    }

    public void toggle() {
        setState(!getState());
    }

    public ExpandComponent createComponent(ModuleComponent parent, int y) {
        return new ExpandComponent(this, parent, y);
    }

    public void toggle(String name, String config) {
        try {
            this.subValues.add(new ToggleValue(name, config, this.owner));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void check(String name, String config) {
        try {
            this.subValues.add(new CheckValue(name, config, this.owner));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void slider(String name, double min, double max, double increment, @Nullable String valueType, String config, Class<?> targetType) {
        try {
            this.subValues.add(new SliderValue(name, min, max, increment, valueType, config, this.owner, targetType));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void mode(String name, List<String> modes, String config) {
        try {
            this.subValues.add(new ModeValue(name, modes, config, this.owner));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void text(@Nullable String name, String config) {
        try {
            this.subValues.add(new TextValue(name, config, this.owner));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void text(@Nullable String name, String description, String config) {
        try {
            this.subValues.add(new TextValue(name, description, config, this.owner));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void color(String name, @Nonnull ColorLink link) {
        try {
            this.subValues.add(new ColorValue(name, link));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void saturation(@Nonnull ColorLink link) {
        try {
            this.subValues.add(new SaturationValue(link));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void brightness(@Nonnull ColorLink link) {
        try {
            this.subValues.add(new BrightnessValue(link));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void opacity(String name, String config) {
        try {
            this.subValues.add(new OpacityValue(name, config, this.owner));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void button(String name, float textScale, Runnable action) {
        try {
            this.subValues.add(new ButtonValue(name, textScale, action));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void bind(String name, String config) {
        try {
            this.subValues.add(new BindValue(name, config, this.owner));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}