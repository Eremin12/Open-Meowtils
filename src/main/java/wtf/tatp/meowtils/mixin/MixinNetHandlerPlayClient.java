package wtf.tatp.meowtils.mixin;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.util.fixes.ResourceExploitFix;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Inject(method = "handleResourcePack", at = @At("HEAD"), cancellable = true)
    private void meowtils$handleResourcePack(S48PacketResourcePackSend packet, CallbackInfo ci) {
        if (!ResourceExploitFix.isValid(packet, (NetHandlerPlayClient) (Object) this)) {
            ci.cancel();
        }
    }
}