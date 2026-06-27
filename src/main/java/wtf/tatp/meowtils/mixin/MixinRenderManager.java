package wtf.tatp.meowtils.mixin;

import net.minecraft.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.module.utility.Freelook;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Shadow
    public float playerViewX;

    @Shadow
    public float playerViewY;

    @Inject(method = "cacheActiveRenderInfo", at = @At("RETURN"))
    private void meowtils$cacheActiveRenderInfo(CallbackInfo ci) {
        if (Freelook.isActive()) {
            this.playerViewX = Freelook.getPitch();
            this.playerViewY = Freelook.getYaw();
        }
    }
}