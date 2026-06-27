package wtf.tatp.meowtils.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import wtf.tatp.meowtils.event.api.Event;

public class AttackEntityEvent extends Event {

    private final EntityPlayer player;
    private final Entity target;

    public AttackEntityEvent(EntityPlayer player, Entity target) {
        this.player = player;
        this.target = target;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public Entity getTarget() {
        return this.target;
    }
}