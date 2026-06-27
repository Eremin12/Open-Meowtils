package wtf.tatp.meowtils.gui.values;

import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.ColorComponent;

public class ColorValue {

    private final String name;
    private final double min = 0.0D;
    private final double max = 360.0D;
    private final ColorLink link;

    public ColorValue(String name, ColorLink link) {
        this.name = name;
        this.link = link;
    }

    public double get() {
        return this.link.getHue() * 360.0D;
    }

    public String getName() {
        return this.name;
    }

    public void set(double newHue) {
        double clamped = clamp(newHue, min, max);
        float h = (float) (clamped / 360.0D);
        this.link.apply(h, this.link.getSaturation(), this.link.getBrightness());
    }

    public ColorLink getLink() {
        return this.link;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public ColorComponent createComponent(ModuleComponent parent, int y) {
        return new ColorComponent(this, parent, y);
    }
}