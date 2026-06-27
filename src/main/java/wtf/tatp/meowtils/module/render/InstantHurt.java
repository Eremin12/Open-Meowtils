package wtf.tatp.meowtils.module.render;

import net.minecraft.entity.player.EntityPlayer;
import wtf.tatp.meowtils.config.Config;
import wtf.tatp.meowtils.event.AttackEntityEvent;
import wtf.tatp.meowtils.event.api.EventTarget;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.util.TeamUtil;

public class InstantHurt extends Module {

    @Config
    public int key = 0;
    @Config
    public boolean enabled = false;

    public InstantHurt() {
        super("InstantHurt", Module.Category.Render);
        tag(Module.ModuleTag.LEGIT);
        tooltip("Instantly render hurt animation on players clientside.");
    }

    @EventTarget
    public void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getTarget() instanceof EntityPlayer)) return;

        EntityPlayer entity = (EntityPlayer) event.getTarget();
        if (TeamUtil.isBot(entity)) return;
        if (entity.hurtResistantTime <= 0) {
            entity.attackEntityFrom(null, 0);
        }
    }
}