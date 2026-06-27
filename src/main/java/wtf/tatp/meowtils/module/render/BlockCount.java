package wtf.tatp.meowtils.module.render;

import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderGameOverlayEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.GuiUtil;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.hudeditor.HudEntry;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.NotificationManager;
import wtf.tatp.meowtils.module.advanced.AutoSwap;
import wtf.tatp.meowtils.module.meowtils.Notifications;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.Util;

public class BlockCount extends Module {

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
    public int threshold = 4;
    @Config
    public boolean alert = true;
    @Config
    public boolean sound = true;
    @Config
    public int posX = 1;
    @Config
    public int posY = 1;

    private static boolean alerted = false;

    public BlockCount() {
        super("BlockCount", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Displays block count on screen.");
        ColorLink color = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Text color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addSlider(new SliderValue("Alert threshold", 1.0D, 16.0D, 1.0D, "blocks", "threshold", this, int.class));
        addToggle(new ToggleValue("Alert when low", "alert", this));
        addToggle(new ToggleValue("Ping sound", "sound", this));
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;

        int totalCount = 0;

        if (this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemBlock && this.mc.currentScreen == null) {
            AutoSwap autoSwap = Module.get(AutoSwap.class);
            if (autoSwap != null && autoSwap.enabled) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack != null && AutoSwap.isValidBlock(stack)) {
                        totalCount += stack.stackSize;
                    }
                }
                if (totalCount == 0) return;
            } else {
                totalCount = this.mc.thePlayer.getHeldItem().stackSize;
            }

            int color = (totalCount <= this.threshold) ? ColorUtil.getRGBFromFormatting(EnumChatFormatting.DARK_RED) : ColorUtil.rgb(this.red, this.green, this.blue);
            Meowtils.drawString(String.valueOf(totalCount), this.posX, this.posY, this.scale, color);
        }

        if (GuiUtil.inEditor()) {
            Meowtils.drawString("64", this.posX, this.posY, this.scale, ColorUtil.rgb(this.red, this.green, this.blue));
        }

        if (totalCount == this.threshold && !alerted) {
            if (this.alert) {
                String end = (this.threshold == 1) ? " block left!" : " blocks left!";

                if (Notifications.getMode() != Notifications.Mode.NOTIFICATION) {
                    Meowtils.addMessage(EnumChatFormatting.RED + "You only have " + totalCount + end);
                }

                if (Notifications.getMode() != Notifications.Mode.CHAT) {
                    NotificationManager.show("BlockCount", EnumChatFormatting.RED + String.valueOf(totalCount) + end, NotificationManager.Type.ALERT, 1500L);
                }
            }

            if (this.sound) {
                Util.playSound(Util.Sound.PING_DEEP, 100);
            }
            alerted = true;
        } else if (totalCount > this.threshold) {
            alerted = false;
        }
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("64", 1, this.scale)));
    }
}