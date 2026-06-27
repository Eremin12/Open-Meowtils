package wtf.tatp.meowtils.module.render;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.mixin.AccessorPlayerControllerMP;
import wtf.tatp.meowtils.module.meowtils.Settings;
import wtf.tatp.meowtils.util.ColorUtil;

public class BreakProgress extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public String mode = "Percentage";
    @Config
    public int red = 255;
    @Config
    public int green = 255;
    @Config
    public int blue = 255;
    @Config
    public float scale = 0.65F;
    @Config
    public float opacity = 100.0F;
    @Config
    public boolean dynamicColor = false;

    private float progress;
    private BlockPos block;
    private String progressStr;

    public BreakProgress() {
        super("BreakProgress", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Displays a countdown until block is broken.");
        addMode(new ModeValue("Mode", Arrays.asList("Percentage", "Time"), "mode", this));
        ColorLink color = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Text color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addOpacity(new OpacityValue("Text opacity", "opacity", this));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addToggle(new ToggleValue("Dynamic color", "dynamicColor", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;
        if (this.mc.objectMouseOver == null || this.mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            resetVariables();
            return;
        }

        this.progress = ((AccessorPlayerControllerMP) this.mc.playerController).getCurBlockDamageMP();
        if (this.progress == 0.0F) {
            resetVariables();
            return;
        }

        this.block = this.mc.objectMouseOver.getBlockPos();
        updateProgressString();
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        if (this.progress == 0.0F || this.block == null) return;

        double x = this.block.getX() + 0.5D - this.mc.getRenderManager().viewerPosX;
        double y = this.block.getY() + 0.5D - this.mc.getRenderManager().viewerPosY;
        double z = this.block.getZ() + 0.5D - this.mc.getRenderManager().viewerPosZ;

        int convertedOpacity = ColorUtil.convertOpacity(this.opacity);

        int color = !this.dynamicColor ? ColorUtil.rgba(this.red, this.green, this.blue, convertedOpacity) :
                ((this.progress > 0.9F) ? ColorUtil.rgba(ColorUtil.DARK_RED.getRed(), ColorUtil.DARK_RED.getGreen(), ColorUtil.DARK_RED.getBlue(), convertedOpacity) :
                 ((this.progress > 0.8F) ? ColorUtil.rgba(ColorUtil.RED.getRed(), ColorUtil.RED.getGreen(), ColorUtil.RED.getBlue(), convertedOpacity) :
                  ((this.progress > 0.7F) ? ColorUtil.rgba(ColorUtil.GOLD.getRed(), ColorUtil.GOLD.getGreen(), ColorUtil.GOLD.getBlue(), convertedOpacity) :
                   ((this.progress > 0.6F) ? ColorUtil.rgba(ColorUtil.YELLOW.getRed(), ColorUtil.YELLOW.getGreen(), ColorUtil.YELLOW.getBlue(), convertedOpacity) :
                           ColorUtil.rgba(this.red, this.green, this.blue, convertedOpacity)))));

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.02F, -0.02F, -0.02F);

        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glEnable(GL11.GL_BLEND);

        Settings settings = Module.get(Settings.class);
        if (settings == null || !settings.smoothFont) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.scale, this.scale, this.scale);
            this.mc.fontRendererObj.drawString(this.progressStr.replace(",", "."), (int)(-this.mc.fontRendererObj.getStringWidth(this.progressStr) / 2.0F / this.scale), (int)(-3.0F / this.scale), color);
            GlStateManager.popMatrix();
        } else {
            Meowtils.fontRenderer.drawScaledStringWithShadow(this.progressStr.replace(",", "."), -Meowtils.fontRenderer.getStringWidth(this.progressStr, 10.0F) / 2.0F, -3.0F, color, this.scale * 10.0F);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    private void updateProgressString() {
        if (this.mode.equals("Percentage")) {
            this.progressStr = (int) (this.progress * 100.0F) + "%";
        } else if (this.mode.equals("Time")) {
            IBlockState state = this.mc.theWorld.getBlockState(this.block);
            Block blockState = state.getBlock();
            float blockHardness = blockState.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.theWorld, this.block);

            if (blockHardness > 0.0F) {
                float breakSpeed = blockState.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.theWorld, this.block);
                if (breakSpeed > 0.0F) {
                    int ticks = (int) Math.ceil(((1.0F - this.progress) / breakSpeed));
                    double timeLeft = ticks / 20.0D;
                    this.progressStr = String.format("%.1f", timeLeft).replace('.', ',') + "s";
                }
            }
        }
    }

    private void resetVariables() {
        this.progress = 0.0F;
        this.block = null;
        this.progressStr = "";
    }
}