package wtf.tatp.meowtils.module.render;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.RenderPlayerEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.gui.values.OpacityValue;
import wtf.tatp.meowtils.module.meowtils.GUI;
import wtf.tatp.meowtils.util.TeamUtil;

public class AntiInvis extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;
    @Config
    public float opacity = 50.0F;

    private static final Set<EntityPlayer> INVISIBLE = Collections.newSetFromMap(new WeakHashMap<>());

    public AntiInvis() {
        super("Anti-Invis", Module.Category.Render);
        tag(Module.ModuleTag.SAFE);
        tooltip("Renders invisible players semi-transparent instead.");
        addOpacity(new OpacityValue("Opacity", "opacity", this));
    }

    @EventTarget
    public void onRenderPlayer(RenderPlayerEvent event) {
        AbstractClientPlayer abstractClientPlayer = event.getPlayer();
        float dividedOpacity = this.opacity / 100.0F;

        if (event.getStage() == RenderPlayerEvent.Stage.PRE) {
            if (!shouldBeTransparent(abstractClientPlayer)) {
                return;
            }
            if (abstractClientPlayer.isInvisible()) {
                abstractClientPlayer.setInvisible(false);
                INVISIBLE.add(abstractClientPlayer);
            }

            if (dividedOpacity < 1.0F) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, dividedOpacity);
            }
        }

        if (event.getStage() == RenderPlayerEvent.Stage.POST) {
            if (INVISIBLE.contains(abstractClientPlayer)) {
                abstractClientPlayer.setInvisible(true);
                INVISIBLE.remove(abstractClientPlayer);
            }

            if (dividedOpacity < 1.0F) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
    }

    private boolean shouldBeTransparent(EntityPlayer player) {
        GUI gui = Module.get(GUI.class);
        if (gui != null && gui.debugMode) return true;
        return (player != this.mc.thePlayer && player.isInvisible() && !TeamUtil.isBot(player));
    }
}