package wtf.tatp.meowtils.mixin;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLivingBase.class)
public interface AccessorEntityLivingBase {

  @Accessor("jumpTicks")
  void setJumpTicks(int jumpTicks);

  @Accessor("jumpTicks")
  int getJumpTicks();
}