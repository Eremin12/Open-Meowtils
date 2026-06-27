package wtf.tatp.meowtils.gui.values;

import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.ButtonComponent;

public class ButtonValue {

    private final String name;
    private final Runnable action;
    private final float scale;

    public ButtonValue(String name, float scale, Runnable action) {
        this.name = name;
        this.scale = scale;
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public float getScale() {
        return this.scale;
    }

    public void click() {
        if (this.action != null) {
            this.action.run();
        }
    }

    public ButtonComponent createComponent(ModuleComponent parent, int y) {
        return new ButtonComponent(this, parent, y);
    }
}