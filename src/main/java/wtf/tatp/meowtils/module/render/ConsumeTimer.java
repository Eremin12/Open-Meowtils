package wtf.tatp.meowtils.module.render;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.ColorUtil;

public class ConsumeTimer extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public int red = 255;
    @Config
    public int green = 255;
    @Config
    public int blue = 255;
    @Config
    public float scale = 0.65F;
    @Config
    public String mode = "Seconds";
    @Config
    public boolean dynamicColor = false;
    @Config
    public int posX = 1;
    @Config
    public int posY = 1;

    private static boolean usingItem = false;
    private static int useTicksLeft = 0;
    private static int lastUseDuration = 0;
    private static int maxUseDuration = 32;

    public ConsumeTimer() {
        super("ConsumeTimer", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Displays a countdown of how much time left to consume an item.");
        ColorLink color = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Text color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addMode(new ModeValue("Mode", Arrays.asList("Ticks", "Seconds"), "mode", this));
        addToggle(new ToggleValue("Dynamic color", "dynamicColor", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        if (this.mc.thePlayer.isUsingItem()) {
            int duration = this.mc.thePlayer.getItemInUseCount();
            ItemStack item = this.mc.thePlayer.getItemInUse();

            if (item != null) {
                int maxDuration = item.getMaxItemUseDuration();

                if (maxDuration <= 32) {
                    if (!usingItem || duration < lastUseDuration) {
                        usingItem = true;
                        maxUseDuration = maxDuration;
                        useTicksLeft = maxDuration - duration;
                    } else {
                        useTicksLeft = maxUseDuration - duration;
                    }

                    lastUseDuration = duration;
                } else {
                    usingItem = false;
                    useTicksLeft = 0;
                    lastUseDuration = 0;
                }
            }
        } else {
            usingItem = false;
            useTicksLeft = 0;
            lastUseDuration = 0;
        }
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) {
            return;
        }

        int color = !this.dynamicColor ? ColorUtil.rgb(this.red, this.green, this.blue) :
                ((useTicksLeft < 6) ? ColorUtil.getRGBFromFormatting(EnumChatFormatting.DARK_RED) :
                 ((useTicksLeft < 12) ? ColorUtil.getRGBFromFormatting(EnumChatFormatting.RED) :
                  ((useTicksLeft < 18) ? ColorUtil.getRGBFromFormatting(EnumChatFormatting.GOLD) :
                   ((useTicksLeft < 24) ? ColorUtil.getRGBFromFormatting(EnumChatFormatting.YELLOW) :
                           ColorUtil.rgb(this.red, this.green, this.blue)))));

        if ((useTicksLeft > 0 && usingItem) || GuiUtil.inEditor()) {
            String text;

            if (this.mode.equals("Ticks")) {
                text = String.valueOf(useTicksLeft);
            } else if (this.mode.equals("Seconds")) {
                double secondsLeft = useTicksLeft / 20.0D;
                text = String.format("%.1f", secondsLeft).replace(",", ".");
            } else {
                return;
            }

            Meowtils.drawString(text, this.posX, this.posY, this.scale, color);
        }
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("30", 1, this.scale)));
    }
}