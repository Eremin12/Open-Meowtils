package wtf.tatp.meowtils.mixin;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.event.RenderPlayerEvent;
import wtf.tatp.meowtils.event.api.EventManager;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Inject(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At("HEAD"))
    private void meowtils$doRenderHead(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        RenderPlayerEvent event = new RenderPlayerEvent((RenderPlayer) (Object) this, player, x, y, z, yaw, partialTicks, RenderPlayerEvent.Stage.PRE);
        EventManager.post(event);
    }

    @Inject(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At("RETURN"))
    private void meowtils$doRenderReturn(AbstractClientPlayer player, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        RenderPlayerEvent event = new RenderPlayerEvent((RenderPlayer) (Object) this, player, x, y, z, yaw, partialTicks, RenderPlayerEvent.Stage.POST);
        EventManager.post(event);
    }
}