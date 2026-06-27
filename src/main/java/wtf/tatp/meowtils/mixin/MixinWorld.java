package wtf.tatp.meowtils.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.bedwars.BedESP;

@Mixin(World.class)
public abstract class MixinWorld {

    @Inject(method = "setBlockState", at = @At("HEAD"))
    private void meowtils$setBlockState(BlockPos pos, IBlockState newState, int flags, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;

        if (!world.isRemote) return;
        if (Minecraft.getMinecraft().theWorld == null) return;

        BedESP bedESP = Module.get(BedESP.class);
        if (bedESP == null || !bedESP.enabled) return;

        BedESP.updateBlocks(pos, newState);
    }
}