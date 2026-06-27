package wtf.tatp.meowtils.module.skywars;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.ChatReceivedEvent;
import wtf.tatp.meowtils.event.RenderWorldLastEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.ColorLink;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.BrightnessValue;
import wtf.tatp.meowtils.gui.values.ColorValue;
import wtf.tatp.meowtils.gui.values.ModeValue;
import wtf.tatp.meowtils.gui.values.SaturationValue;
import wtf.tatp.meowtils.gui.values.ToggleValue;
import wtf.tatp.meowtils.manager.session.Skywars;
import wtf.tatp.meowtils.util.Render;

public class StrengthESP extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public int red = 255;
    @Config
    public int green = 0;
    @Config
    public int blue = 0;
    @Config
    public boolean fillBox = true;
    @Config
    public String mode = "3D";
    @Config
    public boolean fadeOut = true;

    private static final Map<String, Long> RECENT_KILLERS = new HashMap<>();
    private static final String[] KILL_MESSAGES = new String[] { " by ", " to ", " with ", " of ", " from ", " knight ", " for ", " on ", " league " };

    public StrengthESP() {
        super("StrengthESP", Module.Category.Skywars);
        tag(Module.ModuleTag.SAFE);
        tooltip("Renders a box on players who have the Strength effect.");
        addMode(new ModeValue("Mode", Arrays.asList("3D", "2D"), "mode", this));
        ColorLink color = new ColorLink("red", "green", "blue", this);
        addColor(new ColorValue("ESP color", color));
        addSaturation(new SaturationValue(color));
        addBrightness(new BrightnessValue(color));
        addToggle(new ToggleValue("Fill entire box", "fillBox", this));
        addToggle(new ToggleValue("Fade out", "fadeOut", this));
    }

    @EventTarget
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null) return;

        EntityLivingBase camEntity = (EntityLivingBase) this.mc.getRenderViewEntity();
        if (camEntity == null) return;

        double camX = camEntity.lastTickPosX + (camEntity.posX - camEntity.lastTickPosX) * event.getPartialTicks();
        double camY = camEntity.lastTickPosY + (camEntity.posY - camEntity.lastTickPosY) * event.getPartialTicks();
        double camZ = camEntity.lastTickPosZ + (camEntity.posZ - camEntity.lastTickPosZ) * event.getPartialTicks();

        long currentTime = System.currentTimeMillis();
        boolean use3D = this.mode.equalsIgnoreCase("3D");

        for (EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player == this.mc.thePlayer) continue;

            String name = player.getName();
            Long killTime = RECENT_KILLERS.get(name);
            if (killTime == null) continue;

            long elapsed = currentTime - killTime;
            if (elapsed > 5000L) continue;

            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks() - camX;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks() - camY;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks() - camZ;

            int alpha = 255;
            if (this.fadeOut) {
                float fadeProgress = (float) elapsed / 5000.0F;
                alpha = (int) (150.0F * (1.0F - fadeProgress));
            }

            Color color = new Color(this.red, this.green, this.blue, alpha);

            Render.drawEntityBox(player, use3D, this.fillBox, color, true, color, 0.1D, 0.1D, 0.1D);
        }
    }

    @EventTarget
    public void onChatReceived(ChatReceivedEvent event) {
        if (event.getType() != 0) return;
        if (Skywars.GAME.isNotActive()) return;

        String msg = event.getComponent().getUnformattedText();
        if (!msg.endsWith(".")) return;

        String killer = null;

        for (String killMsg : KILL_MESSAGES) {
            int index = msg.lastIndexOf(killMsg);
            if (index != -1) {
                killer = msg.substring(index + killMsg.length(), msg.length() - 1);
                break;
            }
        }
        if (killer == null) return;
        if (!killer.matches("\\w{1,16}")) return;

        RECENT_KILLERS.put(killer, System.currentTimeMillis());
    }

    @Override
    public void onReset() {
        RECENT_KILLERS.clear();
    }
}