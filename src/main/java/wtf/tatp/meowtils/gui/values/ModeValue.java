package wtf.tatp.meowtils.gui.values;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.ReflectUtil;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.ModeComponent;

public class ModeValue {

    private final String name;
    private final List<String> modes;
    private String current;
    private final String fieldName;
    private final Object owner;

    public ModeValue(String name, List<String> modes, String fieldName, Object owner) {
        if (modes.isEmpty()) throw new IllegalArgumentException("Mode list must not be empty!");
        this.name = name;
        this.modes = new ArrayList<>(modes);
        this.owner = owner;
        this.fieldName = fieldName;

        Field field = ReflectUtil.bindField(owner, fieldName, String.class);
        String stored = (String) ReflectUtil.get(field, owner, null);
        if (stored != null && this.modes.contains(stored)) {
            this.current = stored;
        } else {
            this.current = this.modes.get(0);
            ConfigManager.save();
        }
    }

    public String getName() {
        return this.name;
    }

    public List<String> getModes() {
        return Collections.unmodifiableList(this.modes);
    }

    public String getValue() {
        Field field = ReflectUtil.bindField(this.owner, this.fieldName, String.class);
        return (String) ReflectUtil.get(field, this.owner, null);
    }

    public boolean is(String mode) {
        return this.current.equals(mode);
    }

    public void setValue(String value) {
        if (!this.modes.contains(value)) throw new IllegalArgumentException("Mode not found: " + value);
        Field field = ReflectUtil.bindField(this.owner, this.fieldName, String.class);
        ReflectUtil.set(field, this.owner, value);
        ConfigManager.save();
    }

    public void setValue(int index) {
        if (index < 0 || index >= this.modes.size()) throw new IndexOutOfBoundsException("Mode index out of range");
        this.current = this.modes.get(index);
        ConfigManager.save();
    }

    public ModeComponent createComponent(ModuleComponent parent, int y) {
        return new ModeComponent(this, parent, y, getName());
    }
}