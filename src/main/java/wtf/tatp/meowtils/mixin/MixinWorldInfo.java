package wtf.tatp.meowtils.mixin;

import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.render.TimeChanger;

@Mixin(WorldInfo.class)
public abstract class MixinWorldInfo {

    @Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true)
    private void meowtils$getWorldTime(CallbackInfoReturnable<Long> cir) {
        TimeChanger t = Module.get(TimeChanger.class);

        if (t == null || !t.enabled) return;

        if (t.realTime) {
            cir.setReturnValue(TimeChanger.getRealTime());
        } else {
            cir.setReturnValue(TimeChanger.toIngameTime(t.time));
        }
    }
}