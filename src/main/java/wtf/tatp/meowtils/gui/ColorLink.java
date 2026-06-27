package wtf.tatp.meowtils.gui;

import java.awt.Color;
import java.lang.reflect.Field;
import wtf.tatp.meowtils.config.ConfigManager;

public class ColorLink {

    private final Field redField;
    private final Field greenField;
    private final Field blueField;
    private final Object owner;
    private float hue;
    private float saturation;
    private float brightness;
    private int lastRed = -1;
    private int lastGreen = -1;
    private int lastBlue = -1;

    public ColorLink(String red, String green, String blue, Object owner) {
        this.owner = owner;
        this.redField = ReflectUtil.bindField(owner, red, Integer.class);
        this.greenField = ReflectUtil.bindField(owner, green, Integer.class);
        this.blueField = ReflectUtil.bindField(owner, blue, Integer.class);
        syncFromConfig();
    }

    private void syncFromConfig() {
        Integer r = ReflectUtil.get(this.redField, this.owner, null);
        Integer g = ReflectUtil.get(this.greenField, this.owner, null);
        Integer b = ReflectUtil.get(this.blueField, this.owner, null);
        if (r == null || g == null || b == null) return;

        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];

        this.lastRed = r;
        this.lastGreen = g;
        this.lastBlue = b;
    }

    private void checkSync() {
        Integer r = ReflectUtil.get(this.redField, this.owner, null);
        Integer g = ReflectUtil.get(this.greenField, this.owner, null);
        Integer b = ReflectUtil.get(this.blueField, this.owner, null);

        if (r == null || g == null || b == null) return;
        if (r != this.lastRed || g != this.lastGreen || b != this.lastBlue) {
            syncFromConfig();
        }
    }

    public void apply(float hue, float sat, float bri) {
        this.hue = hue;
        this.saturation = sat;
        this.brightness = bri;
        int rgb = Color.HSBtoRGB(hue, sat, bri) & 0xFFFFFF;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;

        this.lastRed = r;
        this.lastGreen = g;
        this.lastBlue = b;

        ReflectUtil.set(this.redField, this.owner, r);
        ReflectUtil.set(this.greenField, this.owner, g);
        ReflectUtil.set(this.blueField, this.owner, b);
        ConfigManager.save();
    }

    public float getHue() {
        checkSync();
        return this.hue;
    }

    public float getSaturation() {
        checkSync();
        return this.saturation;
    }

    public float getBrightness() {
        checkSync();
        return this.brightness;
    }

    public int getPureHueRGB() {
        checkSync();
        return Color.HSBtoRGB(getHue(), 1.0F, 1.0F) & 0xFFFFFF;
    }

    public int getRGB() {
        checkSync();
        return Color.HSBtoRGB(this.hue, this.saturation, this.brightness) & 0xFFFFFF;
    }
}