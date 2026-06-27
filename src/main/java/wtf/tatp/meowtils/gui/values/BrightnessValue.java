package wtf.tatp.meowtils.gui.values;

import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.component.ModuleComponent;
import wtf.tatp.meowtils.gui.component.subcomponents.BrightnessComponent;

public class BrightnessValue {

    private final double min = 0.0D;
    private final double max = 100.0D;
    private final ColorLink link;

    public BrightnessValue(ColorLink link) {
        this.link = link;
    }

    public double get() {
        return this.link.getBrightness() * 100.0D;
    }

    public void set(double newBrightness) {
        double clamped = clamp(newBrightness, min, max);
        float b = (float) (clamped / 100.0D);
        this.link.apply(this.link.getHue(), this.link.getSaturation(), b);
    }

    public ColorLink getLink() {
        return this.link;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public BrightnessComponent createComponent(ModuleComponent parent, int y) {
        return new BrightnessComponent(this, parent, y);
    }
}