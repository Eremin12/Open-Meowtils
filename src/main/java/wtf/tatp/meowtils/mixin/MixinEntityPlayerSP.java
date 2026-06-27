package wtf.tatp.meowtils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import wtf.tatp.meowtils.module.render.Animations;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    protected MixinEntityPlayerSP(World world, GameProfile profile) {
        super(world, profile);
    }

//    @Override
//    public int getMaxInactiveTime() {
//        return Animations.shouldBlock() ? 10 : super.getMaxInactiveTime();
//    }

    @Override
    public boolean isPlayerSleeping() {
        return Animations.shouldBlock() || super.isPlayerSleeping();
    }
}