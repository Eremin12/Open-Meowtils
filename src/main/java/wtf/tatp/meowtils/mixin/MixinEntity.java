package wtf.tatp.meowtils.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.module.utility.Freelook;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "setAngles", at = @At("HEAD"), cancellable = true)
    private void meowtils$setAngles(float yaw, float pitch, CallbackInfo ci) {
        if (Freelook.isActive()) {
            Freelook.applyDelta(yaw, pitch);
            ci.cancel();
        }
    }
}