package wtf.tatp.meowtils.mixin;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.module.utility.Freelook;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo {

    private static float originalYaw;
    private static float originalPitch;

    @Inject(method = "updateRenderInfo", at = @At("HEAD"))
    private static void meowtils$updateRenderInfoHead(EntityPlayer player, boolean isThirdPerson, CallbackInfo ci) {
        if (Freelook.isActive()) {
            originalYaw = player.rotationYaw;
            originalPitch = player.rotationPitch;
            player.rotationYaw = Freelook.getYaw();
            player.rotationPitch = Freelook.getPitch();
        }
    }

    @Inject(method = "updateRenderInfo", at = @At("RETURN"))
    private static void meowtils$updateRenderInfoReturn(EntityPlayer player, boolean isThirdPerson, CallbackInfo ci) {
        if (Freelook.isActive()) {
            player.rotationYaw = originalYaw;
            player.rotationPitch = originalPitch;
        }
    }
}