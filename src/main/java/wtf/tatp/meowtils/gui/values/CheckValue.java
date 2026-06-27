package wtf.tatp.meowtils.gui.values;

import java.lang.reflect.Field;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.ReflectUtil;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.CheckComponent;

public class CheckValue {

    private final String name;
    private final Object owner;
    private final Field configField;

    public CheckValue(String name, String fieldName, Object owner) {
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

    public CheckComponent createComponent(ModuleComponent parent, int y) {
        return new CheckComponent(this, parent, y);
    }
}