package wtf.tatp.meowtils.gui.values;

import java.lang.reflect.Field;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.ReflectUtil;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.BindComponent;

public class BindValue {

    private final String name;
    private int bind;
    private final Object owner;
    private final Field configField;

    public BindValue(String name, String fieldName, Object owner) {
        this.name = name;
        this.owner = owner;
        this.configField = ReflectUtil.bindField(owner, fieldName, Integer.class);
    }

    public String getName() {
        return this.name;
    }

    public int getBind() {
        return (Integer) ReflectUtil.get(this.configField, this.owner, this.bind);
    }

    public void setBind(int bind) {
        if (bind == getBind()) return;
        ReflectUtil.set(this.configField, this.owner, bind);
        ConfigManager.save();
    }

    public BindComponent createComponent(ModuleComponent parent, int y) {
        return new BindComponent(this, parent, y);
    }
}