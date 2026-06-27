package wtf.tatp.meowtils.gui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.ConfigManager;
import wtf.tatp.meowtils.event.api.EventManager;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ButtonValue;
import wtf.tatp.meowtils.gui.values.CheckValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ExpandValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.TextValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.module.meowtils.GUI;
import wtf.tatp.meowtils.module.meowtils.Notifications;


public abstract class Module {

    private static final String ENABLED_MESSAGE = EnumChatFormatting.WHITE + ":" + EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD + " ON";
    private static final String DISABLED_MESSAGE = EnumChatFormatting.WHITE + ":" + EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + " OFF";

    protected Minecraft mc;
    private String name;
    private boolean keyHeld;
    private int key;
    private boolean enabled;
    private final Category category;
    private final ArrayList<ColorValue> rgb;
    private final ArrayList<SaturationValue> saturation;
    private final ArrayList<BrightnessValue> brightness;
    private final ArrayList<ToggleValue> booleans;
    private final ArrayList<CheckValue> checks;
    private final ArrayList<ModeValue> arrays;
    private final ArrayList<TextValue> strings;
    private final ArrayList<SliderValue> values;
    private final ArrayList<OpacityValue> opacity;
    private final ArrayList<ButtonValue> button;
    private final ArrayList<BindValue> bind;
    private final ArrayList<ExpandValue> expand;
    private String tooltip;
    private ModuleTag tag;
    protected String moduleName;
    public boolean alwaysEnabled = false;
    private final List<Object> orderedValues = new ArrayList<>();
    private final Field keyField;
    private final Field stateField;
    private final Object owner;

    public enum ModuleTag {
        LEGIT,
        SAFE,
        BLATANT;
    }

    public enum Category {
        Meowtils,
        Hypixel,
        Skywars,
        Bedwars,
        Render,
        Antisnipe,
        Utility,
        Advanced,
        Extensions;
    }

    public Module(String name, Category category) {
        this.mc = Minecraft.getMinecraft();
        this.booleans = new ArrayList<>();
        this.checks = new ArrayList<>();
        this.values = new ArrayList<>();
        this.arrays = new ArrayList<>();
        this.strings = new ArrayList<>();
        this.rgb = new ArrayList<>();
        this.saturation = new ArrayList<>();
        this.brightness = new ArrayList<>();
        this.opacity = new ArrayList<>();
        this.button = new ArrayList<>();
        this.bind = new ArrayList<>();
        this.expand = new ArrayList<>();
        this.name = name;
        this.category = category;
        this.tooltip = null;
        this.moduleName = name;
        this.owner = this;

        this.keyField = ReflectUtil.bindField(this.owner, "key", Integer.class);
        this.stateField = ReflectUtil.bindField(this.owner, "enabled", Boolean.class);

        this.key = getKey();
        this.enabled = getState();
    }

    public Module(String name, Category category, boolean alwaysEnabled) {
        this(name, category);
        this.alwaysEnabled = alwaysEnabled;

        if (alwaysEnabled) {
            this.enabled = true;
            EventManager.register(this);
        }
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onReset() {}

    public void setState(boolean enabled) {
        if (this.alwaysEnabled && !enabled) return;
        if (this.enabled == enabled) return;

        this.enabled = enabled;

        if (enabled) {
            EventManager.register(this);
            onEnable();
        } else {
            EventManager.unregister(this);
            onDisable();
        }

        if (!this.alwaysEnabled && this.stateField != null) {
            ReflectUtil.set(this.stateField, this.owner, enabled);
            ConfigManager.save();
        }

        if (!this.alwaysEnabled && this.mc.thePlayer != null && get(Notifications.class).toggle) {
            if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                String message = enabled ? (this.moduleName + ENABLED_MESSAGE) : (this.moduleName + DISABLED_MESSAGE);
                Meowtils.addMessage(message);
            }
            if (Notifications.getMode() != Notifications.Mode.CHAT) {
                NotificationManager.show(this.moduleName, enabled ? (EnumChatFormatting.GREEN + "Enabled") : (EnumChatFormatting.RED + "Disabled"), NotificationManager.Type.INFO, 1500L);
            }
        }
    }

    public void addToggle(ToggleValue booleans) {
        this.booleans.add(booleans);
        this.orderedValues.add(booleans);
    }

    public void addCheck(CheckValue checks) {
        this.checks.add(checks);
        this.orderedValues.add(checks);
    }

    public void addMode(ModeValue array) {
        this.arrays.add(array);
        this.orderedValues.add(array);
    }

    public void addText(TextValue textValue) {
        this.strings.add(textValue);
        this.orderedValues.add(textValue);
    }

    public void addSlider(SliderValue values) {
        this.values.add(values);
        this.orderedValues.add(values);
    }

    public void addButton(ButtonValue buttons) {
        this.button.add(buttons);
        this.orderedValues.add(buttons);
    }

    public void addBind(BindValue bind) {
        this.bind.add(bind);
        this.orderedValues.add(bind);
    }

    public void addExpand(ExpandValue expand) {
        this.expand.add(expand);
        this.orderedValues.add(expand);
    }

    public void addColor(ColorValue colorValue) {
        this.rgb.add(colorValue);
        this.orderedValues.add(colorValue);
    }

    public void addSaturation(SaturationValue saturationValue) {
        this.saturation.add(saturationValue);
        this.orderedValues.add(saturationValue);
    }

    public void addBrightness(BrightnessValue brightnessValue) {
        this.brightness.add(brightnessValue);
        this.orderedValues.add(brightnessValue);
    }

    public void addOpacity(OpacityValue opacityValue) {
        this.opacity.add(opacityValue);
        this.orderedValues.add(opacityValue);
    }

    public List<HudEntry> hudEditor() {
        return Collections.emptyList();
    }

    public Module tooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public Module tag(ModuleTag tag) {
        this.tag = tag;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isKeyHeld() {
        return this.keyHeld;
    }

    public void setKeyHeld(boolean keyHeld) {
        this.keyHeld = keyHeld;
    }

    public void setKey(int key) {
        this.key = key;

        if (this.keyField != null) {
            ReflectUtil.set(this.keyField, this.owner, key);
            ConfigManager.save();
        }
    }

    public boolean getState() {
        return (Boolean) ReflectUtil.get(this.stateField, this.owner, false);
    }

    public int getKey() {
        return (Integer) ReflectUtil.get(this.keyField, this.owner, 0);
    }

    public Category getCategory() {
        return this.category;
    }

    public ArrayList<ToggleValue> getBooleans() {
        return this.booleans;
    }

    public ArrayList<CheckValue> getChecks() {
        return this.checks;
    }

    public ArrayList<ModeValue> getArrays() {
        return this.arrays;
    }

    public ArrayList<TextValue> getStrings() {
        return this.strings;
    }

    public ArrayList<SliderValue> getValues() {
        return this.values;
    }

    public ArrayList<ColorValue> getRgb() {
        return this.rgb;
    }

    public ArrayList<SaturationValue> getSaturation() {
        return this.saturation;
    }

    public ArrayList<BrightnessValue> getBrightness() {
        return this.brightness;
    }

    public ArrayList<OpacityValue> getOpacity() {
        return this.opacity;
    }

    public ArrayList<ButtonValue> getButton() {
        return this.button;
    }

    public ArrayList<BindValue> getBind() {
        return this.bind;
    }

    public ArrayList<ExpandValue> getExpand() {
        return this.expand;
    }

    public void toggle() {
        setState(!this.enabled);
    }

    public static ArrayList<Module> getCategoryModules(Category cat) {
        ArrayList<Module> modsInCategory = new ArrayList<>();

        for (Module m : ModuleManager.getModules()) {
            if (m.getCategory() != cat || !GUI.shouldShowModule(m)) continue;
            modsInCategory.add(m);
        }

        return modsInCategory;
    }

    public List<Object> getOrderedValues() {
        return this.orderedValues;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public ModuleTag getTag() {
        return this.tag;
    }

    public static <T extends Module> T get(Class<T> clazz) {
        return ModuleManager.get(clazz);
    }

    public void reset() {
        onReset();
    }
}