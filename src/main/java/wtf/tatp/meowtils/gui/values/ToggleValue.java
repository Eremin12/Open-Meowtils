package wtf.tatp.meowtils.gui.values;

import java.lang.reflect.Field;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.ReflectUtil;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.ToggleComponent;

public class ToggleValue {

    private final String name;
    private final Field configField;
    private final Object owner;

    public ToggleValue(String name, String fieldName, Object owner) {
        this.name = name;
        this.owner = owner;
        this.configField = ReflectUtil.bindField(owner, fieldName, Boolean.class);
    }

    public String getName() {
        return this.name;
    }

    public boolean getState() {
        return (Boolean) ReflectUtil.get(this.configField, this.owner, false);
    }

    public void setState(boolean state) {
        if (state == getState()) return;
        ReflectUtil.set(this.configField, this.owner, state);
        ConfigManager.save();
    }

    public void toggle() {
        setState(!getState());
    }

    public ToggleComponent createComponent(ModuleComponent parent, int y) {
        return new ToggleComponent(this, parent, y);
    }
}