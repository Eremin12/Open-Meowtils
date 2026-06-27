package wtf.tatp.meowtils.module.render;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.util.TeamUtil;

public class HealthESP extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public boolean showSelf = false;

    private static String espMode = "Raven";

    public HealthESP() {
        super("HealthESP", Module.Category.Render);
        tag(Module.ModuleTag.SAFE);
        tooltip("Renders a health bar on players.");
        addToggle(new ToggleValue("Show self", "showSelf", this));
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (!TeamUtil.isBot(player) &&
                    (this.showSelf || player != this.mc.thePlayer) &&
                    (!this.showSelf || player != this.mc.thePlayer || this.mc.gameSettings.thirdPersonView != 0) &&
                    player.hurtTime == 0) {
                renderHealthBar(player, event);
            }
        }
    }

    private void renderHealthBar(EntityPlayer player, RenderWorldLastEvent event) {
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks() - this.mc.getRenderManager().viewerPosX;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks() - this.mc.getRenderManager().viewerPosY;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks() - this.mc.getRenderManager().viewerPosZ;
        double healthRatio = (player.getHealth() / player.getMaxHealth());
        int barHeight = (int) (74.0D * healthRatio);
        barHeight = Math.max(0, Math.min(barHeight, 74));

        int ravenBarColor = (healthRatio < 0.3D) ? Color.red.getRGB() :
                ((healthRatio < 0.5D) ? Color.orange.getRGB() :
                 ((healthRatio < 0.7D) ? Color.yellow.getRGB() : Color.green.getRGB()));

        if (espMode.equals("Raven")) {
            boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            GlStateManager.pushMatrix();

            GL11.glTranslated(x, y - 0.2D, z);
            GL11.glRotatef(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            GL11.glScalef(0.03F, 0.03F, 0.03F);

            Gui.drawRect(21, -1, 25, 75, Color.black.getRGB());
            Gui.drawRect(22, barHeight, 24, 74, Color.darkGray.getRGB());
            Gui.drawRect(22, 0, 24, barHeight, ravenBarColor);

            if (depthEnabled) {
                GlStateManager.disableBlend();
            } else {
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            }

            GlStateManager.popMatrix();
        }
    }
}