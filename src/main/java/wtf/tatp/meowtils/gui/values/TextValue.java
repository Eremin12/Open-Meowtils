package wtf.tatp.meowtils.gui.values;

import java.lang.reflect.Field;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.ReflectUtil;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.TextComponent;

public class TextValue {

    private final String name;
    private final String description;
    private final Field configField;
    private final Object owner;

    public TextValue(String name, String fieldName, Object owner) {
        this(name, "Type...", fieldName, owner);
    }

    public TextValue(String name, String description, String fieldName, Object owner) {
        this.name = name;
        this.owner = owner;
        this.configField = ReflectUtil.bindField(owner, fieldName, String.class);
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String get() {
        return (String) ReflectUtil.get(this.configField, this.owner, "");
    }

    public void set(String value) {
        if (value == null) value = "";
        if (value.equals(get())) return;
        ReflectUtil.set(this.configField, this.owner, value);
        ConfigManager.save();
    }

    public TextComponent createComponent(ModuleComponent parent, int y) {
        return new TextComponent(this, parent, y, getName());
    }
}