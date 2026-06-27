package wtf.tatp.meowtils.module.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.Meowtils;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.AttackEntityEvent;
import wtf.tatp.meowtils.event.ClientTickEvent;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.SliderValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.module.meowtils.Settings;
import wtf.tatp.meowtils.util.ColorUtil;
import wtf.tatp.meowtils.util.DelayedTask;

public class DamageTags extends Module {

    @Config
    public boolean enabled = false;
    @Config
    public int key = 0;
    @Config
    public int red = 255;
    @Config
    public int green = 255;
    @Config
    public int blue = 255;
    @Config
    public float scale = 0.65F;
    @Config
    public boolean dynamicColor = true;
    @Config
    public float expireTime = 2.0F;
    @Config
    public boolean fadeOut = false;
    @Config
    public boolean suffix = true;

    private static final Map<EntityPlayer, Float> HEALTH = new HashMap<>();
    private static final List<Indicator> INDICATORS = new ArrayList<>();
    private static boolean attacked = false;

    public DamageTags() {
        super("DamageTags", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Displays damage dealt after hitting someone.");
        ColorLink colorLink = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("Text color", colorLink));
        addSaturation(new SaturationValue(colorLink));
        addBrightness(new BrightnessValue(colorLink));
        addSlider(new SliderValue("Scale", 0.5D, 1.5D, 0.05D, null, "scale", this, float.class));
        addSlider(new SliderValue("Expire", 0.5D, 5.0D, 0.1D, "s", "expireTime", this, float.class));
        addToggle(new ToggleValue("Dynamic color", "dynamicColor", this));
        addToggle(new ToggleValue("Fade out", "fadeOut", this));
        addToggle(new ToggleValue("Show suffix", "suffix", this));
    }

    @EventTarget
    public void onClientTick(ClientTickEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null || event.getPhase() != ClientTickEvent.Phase.POST) return;

        HEALTH.keySet().removeIf(player -> !this.mc.theWorld.playerEntities.contains(player));

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == this.mc.thePlayer) continue;
            HEALTH.putIfAbsent(player, player.getHealth());
        }
    }

    @EventTarget
    public void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getTarget() instanceof EntityPlayer)) return;

        EntityPlayer target = (EntityPlayer) event.getTarget();
        float preHealth = HEALTH.getOrDefault(target, target.getHealth());

        if (!attacked) {
            attacked = true;
            new DelayedTask(() -> {
                attacked = false;
                float postHealth = target.getHealth();
                float damage = preHealth - postHealth;
                if (damage > 0.0F) {
                    double x = target.posX;
                    double y = target.posY + target.getEyeHeight() - 0.3D;
                    double z = target.posZ;
                    INDICATORS.add(new Indicator(x, y, z, damage, System.currentTimeMillis()));
                    HEALTH.put(target, postHealth);
                }
            }, 1);
        }
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;

        long now = System.currentTimeMillis();
        float lifespan = this.expireTime * 1000.0F;

        INDICATORS.removeIf(indicator -> ((float) (now - indicator.time) > lifespan));

        for (Indicator indicator : INDICATORS) {
            double x = indicator.x - this.mc.getRenderManager().viewerPosX;
            double y = indicator.y - this.mc.getRenderManager().viewerPosY;
            double z = indicator.z - this.mc.getRenderManager().viewerPosZ;

            long elapsed = now - indicator.time;
            int alpha = 255;
            if (this.fadeOut) {
                float fadeProgress = (float) elapsed / lifespan;
                alpha = (int) (150.0F * (1.0F - fadeProgress));
            }

            String damage = String.format("%.1f", indicator.damage).replace(",", ".") + (this.suffix ? (EnumChatFormatting.RED + "❤") : "");

            Color dynamic = (indicator.damage < 1.0F) ? ColorUtil.getColorFromFormatting(EnumChatFormatting.GREEN) :
                    ((indicator.damage < 2.0F) ? ColorUtil.getColorFromFormatting(EnumChatFormatting.DARK_GREEN) :
                     ((indicator.damage < 4.0F) ? ColorUtil.getColorFromFormatting(EnumChatFormatting.YELLOW) :
                      ((indicator.damage < 6.0F) ? ColorUtil.getColorFromFormatting(EnumChatFormatting.RED) :
                              ColorUtil.getColorFromFormatting(EnumChatFormatting.DARK_RED))));

            int color = this.dynamicColor ? ColorUtil.rgba(dynamic.getRed(), dynamic.getGreen(), dynamic.getBlue(), alpha) :
                    ColorUtil.rgba(this.red, this.green, this.blue, alpha);

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
                this.mc.fontRendererObj.drawString(damage, (int)(-this.mc.fontRendererObj.getStringWidth(damage) / 2.0F / this.scale), (int)(-3.0F / this.scale), color);
                GlStateManager.popMatrix();
            } else {
                Meowtils.fontRenderer.drawScaledStringWithShadow(damage, -Meowtils.fontRenderer.getStringWidth(damage, 10.0F) / 2.0F, -3.0F, color, this.scale * 10.0F);
            }

            GL11.glDisable(GL11.GL_BLEND);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }

    private static class Indicator {
        private final double x;
        private final double y;
        private final double z;
        private final float damage;
        private final long time;

        private Indicator(double x, double y, double z, float damage, long time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.damage = damage;
            this.time = time;
        }
    }

    @Override
    public void onReset() {
        HEALTH.clear();
        INDICATORS.clear();
        attacked = false;
    }
}