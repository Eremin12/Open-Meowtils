package wtf.tatp.meowtils.event;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import wtf.tatp.meowtils.event.api.Event;

public class EntityJoinWorldEvent extends Event {

    private final Entity entity;
    private final World world;

    public EntityJoinWorldEvent(Entity entity, World world) {
        this.entity = entity;
        this.world = world;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public World getWorld() {
        return this.world;
    }
}