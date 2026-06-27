package wtf.tatp.meowtils.module.utility;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.GuiOpenEvent;
import wtf.tatp.meowtils.event.RenderTickEvent;
import wtf.tatp.meowtils.event.WorldEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BindValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;

public class Freelook extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int freelookKey = 0;
    @Config
    public boolean customFov = false;
    @Config
    public int fov = 70;
    @Config
    public String startingPos = "Forward";
    @Config
    public String mode = "Hold";

    private static boolean perspectiveToggled = false;
    private static boolean previousState = false;
    private static float cameraYaw = 0.0F;
    private static float cameraPitch = 0.0F;
    private static float previousFov = 0.0F;
    private static int previousPerspective = 0;
    private static boolean keyToggled = false;

    public Freelook() {
        super("Freelook", Module.Category.Utility);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Allows you to look around in third person without changing your serverside view.");
        addMode(new ModeValue("Mode", Arrays.asList("Hold", "Toggle"), "mode", this));
        addMode(new ModeValue("Start Position", Arrays.asList("Forward", "Backwards"), "startingPos", this));
        addToggle(new ToggleValue("Custom fov", "customFov", this));
        addSlider(new SliderValue("Fov", 30.0D, 110.0D, 5.0D, null, "fov", this, int.class));
        addBind(new BindValue("Bind", "freelookKey", this));
    }

    @EventTarget
    public void onRenderTick(RenderTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != RenderTickEvent.Phase.POST) return;
        if (this.mc.currentScreen != null) return;

        boolean down = Keyboard.isKeyDown(this.freelookKey);

        if (down != previousState) {
            if (this.mode.equals("Hold")) {
                onPressed(down);
            } else if (this.mode.equals("Toggle") && down) {
                keyToggled = !keyToggled;
                onPressed(keyToggled);
            }

            previousState = down;
        }
    }

    @EventTarget
    public void onWorldLoad(WorldEvent event) {
        if (event.getType() != WorldEvent.Type.LOAD) return;
        if (perspectiveToggled) {
            resetPerspective();
        }
    }

    @EventTarget
    public void onGuiOpen(GuiOpenEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (event.getGui() == null) return;

        if (perspectiveToggled) {
            resetPerspective();
        }
    }

    private void onPressed(boolean down) {
        if (down) {
            cameraYaw = this.mc.thePlayer.rotationYaw;
            cameraPitch = this.mc.thePlayer.rotationPitch;

            if (perspectiveToggled) {
                resetPerspective();
            } else {
                enterPerspective();
            }

            this.mc.entityRenderer.setupOverlayRendering();
        } else {
            resetPerspective();
        }
    }

    private void enterPerspective() {
        perspectiveToggled = true;

        previousFov = this.mc.gameSettings.fovSetting;
        previousPerspective = this.mc.gameSettings.thirdPersonView;

        if (this.customFov) {
            this.mc.gameSettings.fovSetting = this.fov;
        }

        if (this.startingPos.equals("Backwards")) {
            this.mc.gameSettings.thirdPersonView = 2;
        } else {
            this.mc.gameSettings.thirdPersonView = 1;
        }
    }

    private void resetPerspective() {
        perspectiveToggled = false;
        keyToggled = false;

        this.mc.gameSettings.fovSetting = previousFov;
        this.mc.gameSettings.thirdPersonView = previousPerspective;

        this.mc.entityRenderer.setupOverlayRendering();
    }

    public static void applyDelta(float yaw, float pitch) {
        if (!perspectiveToggled) return;

        cameraYaw += yaw * 0.15F;
        cameraPitch -= pitch * 0.15F;
        cameraPitch = Math.max(-90.0F, Math.min(90.0F, cameraPitch));
        Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
    }

    public static boolean isActive() {
        return perspectiveToggled;
    }

    public static float getYaw() {
        return cameraYaw;
    }

    public static float getPitch() {
        return cameraPitch;
    }

    @Override
    public void onDisable() {
        if (perspectiveToggled) resetPerspective();
    }
}