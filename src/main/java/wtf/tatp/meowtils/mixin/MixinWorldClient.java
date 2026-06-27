package wtf.tatp.meowtils.mixin;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.event.EntityJoinWorldEvent;
import wtf.tatp.meowtils.event.api.EventManager;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient {

    @Inject(method = "spawnEntityInWorld", at = @At("HEAD"))
    private void meowtils$spawnEntityInWorld(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        EntityJoinWorldEvent event = new EntityJoinWorldEvent(entity, (World) (Object) this);
        EventManager.post(event);
    }
}