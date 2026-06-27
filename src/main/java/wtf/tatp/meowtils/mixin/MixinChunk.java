package wtf.tatp.meowtils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.meowtils.gui.Module;
import wtf.tatp.meowtils.module.bedwars.BedESP;

@Mixin(Chunk.class)
public abstract class MixinChunk {

    @Inject(method = "fillChunk", at = @At("TAIL"))
    private void meowtils$fillChunk(byte[] data, int mask, boolean hasSkyLight, CallbackInfo ci) {
        if (!Minecraft.getMinecraft().theWorld.isRemote) return;

        BedESP bedESP = Module.get(BedESP.class);
        if (bedESP == null || !bedESP.enabled) return;

        BedESP.updateChunk((Chunk) (Object) this);
    }
}