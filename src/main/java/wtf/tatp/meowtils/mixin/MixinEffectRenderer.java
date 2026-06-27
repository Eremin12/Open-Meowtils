package wtf.tatp.meowtils.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.utility.NoParticles;

@Mixin(EffectRenderer.class)
public class MixinEffectRenderer {

    @Inject(method = "addBlockHitEffects", at = @At("HEAD"), cancellable = true)
    private void meowtils$addBlockHitEffects(BlockPos pos, EnumFacing facing, CallbackInfo ci) {
        NoParticles noParticles = Module.get(NoParticles.class);
        if (noParticles != null && noParticles.enabled && noParticles.removeBreak && !NoParticles.isRedstoneBlock(pos)) {
            ci.cancel();
        }
    }

    @Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
    private void meowtils$addBlockDestroyEffects(BlockPos pos, IBlockState state, CallbackInfo ci) {
        NoParticles noParticles = Module.get(NoParticles.class);
        if (noParticles != null && noParticles.enabled && noParticles.removeBreak && !NoParticles.isRedstoneBlock(pos)) {
            ci.cancel();
        }
    }
}