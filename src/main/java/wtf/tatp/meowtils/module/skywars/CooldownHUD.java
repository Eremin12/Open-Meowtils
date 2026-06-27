package wtf.tatp.meowtils.module.skywars;

import java.util.Collections;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
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
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.DelayedTask;

public class CooldownHUD extends Module {

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
    public boolean hideAfterCooldown = false;
    @Config
    public int posX = 1;
    @Config
    public int posY = 1;

    private static int cooldown = 0;
    private static boolean ready = false;
    private static boolean thrownPearl = false;
    private static int tickCounter = 0;
    private static String lastActionbar = "";
    private static String itemName = "";

    public CooldownHUD() {
        super("CooldownHUD", Module.Category.Skywars);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Display item cooldowns on screen.");
        ColorLink color = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Text color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addToggle(new ToggleValue("Hide after cooldown", "hideAfterCooldown", this));
    }

    @EventTarget
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) return;
        if (!GuiUtil.inEditor() && !Skywars.GAME.isActive()) return;

        int color = ColorUtil.rgb(this.red, this.green, this.blue);

        if (GuiUtil.inEditor()) {
            Meowtils.drawString("Corrupt Pearl: " + EnumChatFormatting.GREEN + "Ready", this.posX, this.posY, this.scale, color);
            return;
        }

        if (!itemName.isEmpty()) {
            String displayCooldown = ready ? (EnumChatFormatting.GREEN + "Ready") :
                    (thrownPearl ? (EnumChatFormatting.RED + "Thrown") :
                     (EnumChatFormatting.RED.toString() + cooldown + "s"));

            if (ready && this.hideAfterCooldown) {
                return;
            }
            Meowtils.drawString(itemName + displayCooldown, this.posX, this.posY, this.scale, color);
        }
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        String message = event.getComponent().getUnformattedText();

        if (event.getType() == 2) {
            lastActionbar = message;
        } else if (event.getType() == 0 && message.equals("The game starts in 1 second!")) {
            if (lastActionbar.contains("Enderman")) {
                new DelayedTask(() -> startCooldown("Corrupt Pearl: ", 30), 20);
            } else if (lastActionbar.contains("Enderchest")) {
                new DelayedTask(() -> startCooldown("Enderchest: ", 60), 20);
            } else if (lastActionbar.contains("End Lord")) {
                new DelayedTask(() -> startCooldown("End Lord: ", 30), 20);
            } else if (lastActionbar.contains("Cryomancer")) {
                new DelayedTask(() -> startCooldown("Ice Bridge: ", 30), 20);
            } else if (lastActionbar.contains("Chronobreaker")) {
                new DelayedTask(() -> startCooldown("Echo: ", 8), 20);
            }
        }
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) {
            return;
        }

        if (cooldown > 0) {
            tickCounter++;

            if (tickCounter >= 20) {
                cooldown--;
                tickCounter = 0;

                if (cooldown <= 0) {
                    ready = true;
                }
            }
        }

        if (Mouse.isButtonDown(1)) {
            ItemStack held = this.mc.thePlayer.getHeldItem();

            if (held != null && held.getItem() == Items.ender_pearl && held.isItemEnchanted() && itemName.equals("Corrupt Pearl: ")) {
                if (ready) {
                    thrownPearl = true;
                    ready = false;
                }
            } else if (held != null && held.getItem() == Items.nether_star && itemName.equals("Echo: ") && ready) {
                ready = false;
                startCooldown("Echo: ", 40);
            }
        }
    }

    private void startCooldown(String name, int seconds) {
        itemName = name;
        cooldown = seconds;
        ready = false;
        tickCounter = 0;
    }

    @Override
    public List<HudEntry> hudEditor() {
        return Collections.singletonList(new HudEntry(null, this, "posX", "posY", () -> GuiUtil.getHudBounds("Corrupt Pearl: Ready ", 1, this.scale)));
    }

    @Override
    public void onReset() {
        cooldown = 0;
        ready = false;
        thrownPearl = false;
        tickCounter = 0;
        itemName = "";
        lastActionbar = "";
    }
}