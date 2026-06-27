package wtf.tatp.meowtils.gui.values;

import java.lang.reflect.Field;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.gui.ReflectUtil;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.OpacityComponent;

public class OpacityValue {

    private final String name;
    private final double min;
    private final double max;
    private final double increment;
    private final String valueType;
    private double value;
    private final Field configField;
    private final String fieldName;
    private final Object owner;
    private final Class<?> targetType;

    public OpacityValue(String name, String fieldName, Object owner) {
        this.name = name;
        this.min = 0.0D;
        this.max = 100.0D;
        this.increment = 5.0D;
        this.valueType = "%";
        this.targetType = float.class;
        this.owner = owner;
        this.fieldName = fieldName;
        this.configField = ReflectUtil.bindField(owner, fieldName, float.class);

        syncFromConfig();
    }

    public String getName() {
        return this.name;
    }

    public double get() {
        Number n = (Number) ReflectUtil.get(this.configField, this.owner, null);
        if (n != null) {
            this.value = snap(n.doubleValue());
        }
        return this.value;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public String getValueType() {
        return this.valueType;
    }

    public void set(double newValue) {
        this.value = snap(clamp(newValue, this.min, this.max));
        Field field = ReflectUtil.bindField(this.owner, this.fieldName, float.class);
        ReflectUtil.set(field, this.owner, (float) this.value);
        ConfigManager.save();
    }

    public void syncFromConfig() {
        Number n = (Number) ReflectUtil.get(this.configField, this.owner, null);
        if (n != null) {
            this.value = snap(n.doubleValue());
        }
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double snap(double v) {
        double steps = Math.round((v - this.min) / this.increment);
        double snapped = this.min + steps * this.increment;

        int decimals = Math.max(0, getDecimalPlaces(this.increment));
        double scale = Math.pow(10.0D, decimals);
        return Math.round(snapped * scale) / scale;
    }

    private int getDecimalPlaces(double value) {
        String text = Double.toString(value);
        int index = text.indexOf('.');
        return (index < 0) ? 0 : (text.length() - index - 1);
    }

    public String getFormattedValue() {
        if (this.targetType == int.class || this.targetType == Integer.class || this.value == Math.floor(this.value)) {
            return String.valueOf((int) this.value);
        }

        int decimals = 0;
        double inc = this.increment;
        while (inc < 1.0D && decimals < 6) {
            inc *= 10.0D;
            decimals++;
        }

        String raw = String.format("%." + decimals + "f", this.value);
        raw = raw.replaceAll("0*$", "").replaceAll("\\.$", "");

        return raw;
    }

    public OpacityComponent createComponent(ModuleComponent parent, int y) {
        return new OpacityComponent(this, parent, y);
    }
}